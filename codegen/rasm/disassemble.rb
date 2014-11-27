root_dir = '/u/workdir/codes/rfsc/codegen/target/test-classes'
clazz = 'com.mulberry.athena.asm.DemoClass$A'

ACC_PUBLIC = 0x0001 # class, field, method
ACC_PRIVATE = 0x0002 # class, field, method
ACC_PROTECTED = 0x0004 # class, field, method
ACC_STATIC = 0x0008; # field, method
ACC_FINAL = 0x0010; # class, field, method
ACC_SUPER = 0x0020; # class
ACC_SYNCHRONIZED = 0x0020; # method
ACC_VOLATILE = 0x0040; # field
ACC_BRIDGE = 0x0040; # method
ACC_VARARGS = 0x0080; # method
ACC_TRANSIENT = 0x0080; # field
ACC_NATIVE = 0x0100; # method
ACC_INTERFACE = 0x0200; # class
ACC_ABSTRACT = 0x0400; # class, method
ACC_STRICT = 0x0800; # method
ACC_SYNTHETIC = 0x1000; # class, field, method
ACC_ANNOTATION = 0x2000; # class
ACC_ENUM = 0x4000; # class(?) field inner

ACC_DEPRECATED = 0x20000; # class, field, method

ACC = {
    public: 0x0001,
    private: 0x0002,
    protected: 0x0004,
    static: 0x0008,
    final: 0x0010,
    #super: 0x0020,
    #synchronized: 0x0020
    interface: 0x0200,
    abstract: 0x0400
}

def acc(flags)
  val = ''
  ACC.each do|name, flag|
    if flag & flags != 0
      val << name.to_s
      val << ' '
    end
  end
  val
end



class ContentTypeInfo
  attr_reader :name, :tag
  def initialize(name, tag)
    @name, @tag = name, tag
  end
end

class_file = "#{root_dir}/#{clazz.gsub('.', '/')}.class"
puts class_file
CONSTANT_TYPES = {
  Utf8: 1,
  Integer: 3,
  Float: 4,
  Long: 5,
  Double: 6,
  Class: 7,
  String: 8,
  Fieldref: 9,
  Methodref: 10,
  InterfaceMethodref: 11,
  NameAndType: 12
}

CONSTANT_TYPE_ARRAY = []
CONSTANT_TYPES.each { |name, value| CONSTANT_TYPE_ARRAY[value] = ContentTypeInfo.new(name, value) }


def do_read(io, tag)
  case tag
    when 1
      len = io.read(2).unpack('n')[0]
      io.read(len)
    when 5, 6
      io.read(8)
    when 7, 8
      io.read(2)
    when 3, 4, 9, 10, 11, 12
      io.read(4)
    else
      puts "Unsupported tag #{tag}"
  end
end

def ref(cp, str, tag)
  case tag
    when 1
      str.unpack('a*')[0]
    when 3
      str.unpack('N')[0]
    when 4
      str.unpack('g')[0]
    when 5
      h, l = str.unpack('NN')
      "#{(h << 32) + l}l"
    when 6
      "#{str.unpack('G')[0]}d"
    when 7, 8
      cp.ref(str.unpack('n')[0])
    when 9, 10, 11
      cp.refs(str.unpack('nn'), '.')
    when 12
      cp.refs(str.unpack('nn'))
    else
      puts "Unsupported tag #{tag}"
  end
end

class RefHash < Hash
  def ref(index)
    Ref.new(self, index)
  end
  def refs(indexes, split = ':')
    Refs.new(indexes.map{|index| ref(index) }, split)
  end
end

class Ref
  attr_reader :info
  def initialize(container, index)
    @container =  container
    @index = index
    @info = info
  end

  def to_s
    "##{@index}"
  end

  def val
    val = @container[@index][:ref]
    if val.respond_to?(:val)
      val.val
    else
      val
    end
  end
end

class Refs < Ref
  def initialize(refs, split)
    @refs = refs
    @info = info
    @split = split
  end

  def to_s
    @refs.join(@split)
  end

  def val
    @refs.map{|ref| ref.val}.join(@split)
  end
end

TYPES = {
    Z: 'boolean',
    B: 'byte',
    C: 'char',
    S: 'short',
    I: 'int',
    F: 'float',
    J: 'long',
    D: 'double',
    L: lambda{|ref| "L#{ref}"},
    '['.to_sym => lambda{|type| "[#{type}"}
}


open class_file, 'rb' do|io|
  magic = io.read(4).unpack('N')[0]
  if magic == 0xCAFEBABE
    minor, major = io.read(4).unpack('nn')
    puts "version: #{major}.#{minor}"
    constant_pool_count = io.read(2).unpack('n')[0]
    puts "Constant pool(#{constant_pool_count - 1}):"
    cp = RefHash.new
    no = 1
    while no < constant_pool_count
      tag = io.read(1).unpack('C')[0]
      info = CONSTANT_TYPE_ARRAY[tag]
      if info
        cp[no] = {ref: ref(cp, do_read(io, info.tag), tag), tag: tag, name: info.name}
        if tag == 5 || tag == 6
          no += 1
        end
      end
      no += 1
    end
    cp.each do|i, e|
      ref = e[:ref]
      puts "\t#%02d = %-16s %-20s #{"// #{ref.val}" if ref.respond_to?(:val)}" % [i, e[:name], ref]
    end

    access_flags, this_class, super_class, interfaces_count = io.read(8).unpack('n*')
    class_info = '%s class %s extends %s' % [acc(access_flags), cp[this_class][:ref].val, cp[super_class][:ref].val]

    if interfaces_count > 0
      interfaces = io.read(2 * interfaces_count).unpack('n*')
      class_info << " implements #{interfaces.map{|i| cp[i][:ref].val}.join(', ')}"
    end
    class_info << " {\n"

    class_info << "\n}"
    puts class_info

    fields_count = io.read(2).unpack('n')[0]
    puts "Fields(#{fields_count}):"

    if fields_count > 0
      fields_count.times do
        access_flags, name_index, descriptor_index, attributes_count = io.read(8).unpack('n*')
        puts "\t access_flags #{access_flags}, name: #{cp[name_index][:ref]}, descriptor: #{cp[descriptor_index][:ref]}"
        if attributes_count > 0
          puts "\t Attributes(#{attributes_count}):"
          attribute_name_index, attribute_length = io.read(6).unpack('nN')
          case cp[attribute_name_index][:ref]
            when 'ConstantValue'
              attributes_count.times do
                constantvalue_index = io.read(2).unpack('n')[0]
                puts "\t\tname: ConstantValue, length: #{attribute_length}, value: #{cp[constantvalue_index][:ref]}"
              end
            else
              puts "unknown #{cp[name_index][:ref]}"
          end
        end
      end
    end

    methods_count = io.read(2).unpack('n')[0]
    puts "Methods(#{methods_count}):"
    if methods_count > 0
      methods_count.times do
        access_flags, name_index ,descriptor_index, attributes_count = io.read(8).unpack('n*')
        puts "\t access_flags #{access_flags}, name: #{cp[name_index][:ref]}, descriptor: #{cp[descriptor_index][:ref]}"
        if attributes_count > 0
          puts "\t Attributes(#{attributes_count}):"
          attribute_name_index, attribute_length = io.read(6).unpack('nN')
          attributes = io.read(attribute_length)
          case cp[attribute_name_index][:ref]
            when 'Code'
              attributes_count.times do
                p attributes
                #max_stack, max_locals, code_length, code_code_length, exception_table_length = io.read(11).unpack('nnNCn')
                #constantvalue_index = io.read(2).unpack('n')[0]
                #puts "\t\tname: ConstantValue, length: #{attribute_length}, value: #{cp[constantvalue_index][:ref]}"
              end
            else
              puts "unknown #{cp[name_index][:ref]}"
          end
        end
      end
    end

    attributes_count = io.read(2).unpack('n')[0]
    puts "Attributes(#{attributes_count}):"

    attributes_count.times do
      attribute_name_index, attribute_length = io.read(6).unpack('nN')
      attributes = io.read(attribute_length).unpack('A*')
      puts "attribute_name: #{cp[attribute_name_index][:ref]}, attributes: #{attributes}"
    end

    puts io.read(100)
  else
    raise "magic #{magic} is not valid java class file."
  end

#  i = 0
#  io.each_byte { |byte| puts "#{i} : #{byte.to_s(16)}"; i += 1}
end


=begin
boolean       Z
char          C
byte          B
short         S
int           I
float         F
long          J
double        D
Object        Ljava/lang/Object;
int[]         [I
Object[][]    [[Ljava/lang/Object;


ClassFile {
    u4 magic;
    u2 minor_version;
    u2 major_version;
    u2 constant_pool_count;
    cp_info constant_pool[constant_pool_count-1];
    u2 access_flags;
    u2 this_class;
    u2 super_class;
    u2 interfaces_count;
    u2 interfaces[interfaces_count];
    u2 fields_count;
    field_info fields[fields_count];
    u2 methods_count;
    method_info methods[methods_count];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}

=end


