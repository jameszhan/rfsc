/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package clojure.asm.tree.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import clojure.asm.tree.AbstractInsnNode;
import clojure.asm.tree.InvokeDynamicInsnNode;

/**
 * An {@link Interpreter} for {@link clojure.asm.tree.analysis.SourceValue} values.
 * 
 * @author Eric Bruneton
 */
public class SourceInterpreter extends Interpreter<clojure.asm.tree.analysis.SourceValue> implements
        clojure.asm.Opcodes {

    public SourceInterpreter() {
        super(ASM4);
    }

    protected SourceInterpreter(final int api) {
        super(api);
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue newValue(final clojure.asm.Type type) {
        if (type == clojure.asm.Type.VOID_TYPE) {
            return null;
        }
        return new clojure.asm.tree.analysis.SourceValue(type == null ? 1 : type.getSize());
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue newOperation(final clojure.asm.tree.AbstractInsnNode insn) {
        int size;
        switch (insn.getOpcode()) {
        case LCONST_0:
        case LCONST_1:
        case DCONST_0:
        case DCONST_1:
            size = 2;
            break;
        case LDC:
            Object cst = ((clojure.asm.tree.LdcInsnNode) insn).cst;
            size = cst instanceof Long || cst instanceof Double ? 2 : 1;
            break;
        case GETSTATIC:
            size = clojure.asm.Type.getType(((clojure.asm.tree.FieldInsnNode) insn).desc).getSize();
            break;
        default:
            size = 1;
        }
        return new clojure.asm.tree.analysis.SourceValue(size, insn);
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue copyOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final clojure.asm.tree.analysis.SourceValue value) {
        return new clojure.asm.tree.analysis.SourceValue(value.getSize(), insn);
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue unaryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final clojure.asm.tree.analysis.SourceValue value) {
        int size;
        switch (insn.getOpcode()) {
        case LNEG:
        case DNEG:
        case I2L:
        case I2D:
        case L2D:
        case F2L:
        case F2D:
        case D2L:
            size = 2;
            break;
        case GETFIELD:
            size = clojure.asm.Type.getType(((clojure.asm.tree.FieldInsnNode) insn).desc).getSize();
            break;
        default:
            size = 1;
        }
        return new clojure.asm.tree.analysis.SourceValue(size, insn);
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue binaryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final clojure.asm.tree.analysis.SourceValue value1, final clojure.asm.tree.analysis.SourceValue value2) {
        int size;
        switch (insn.getOpcode()) {
        case LALOAD:
        case DALOAD:
        case LADD:
        case DADD:
        case LSUB:
        case DSUB:
        case LMUL:
        case DMUL:
        case LDIV:
        case DDIV:
        case LREM:
        case DREM:
        case LSHL:
        case LSHR:
        case LUSHR:
        case LAND:
        case LOR:
        case LXOR:
            size = 2;
            break;
        default:
            size = 1;
        }
        return new clojure.asm.tree.analysis.SourceValue(size, insn);
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue ternaryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final clojure.asm.tree.analysis.SourceValue value1, final clojure.asm.tree.analysis.SourceValue value2,
            final clojure.asm.tree.analysis.SourceValue value3) {
        return new clojure.asm.tree.analysis.SourceValue(1, insn);
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue naryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final List<? extends clojure.asm.tree.analysis.SourceValue> values) {
        int size;
        int opcode = insn.getOpcode();
        if (opcode == MULTIANEWARRAY) {
            size = 1;
        } else {
            String desc = (opcode == INVOKEDYNAMIC) ? ((InvokeDynamicInsnNode) insn).desc
                    : ((clojure.asm.tree.MethodInsnNode) insn).desc;
            size = clojure.asm.Type.getReturnType(desc).getSize();
        }
        return new clojure.asm.tree.analysis.SourceValue(size, insn);
    }

    @Override
    public void returnOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final clojure.asm.tree.analysis.SourceValue value, final clojure.asm.tree.analysis.SourceValue expected) {
    }

    @Override
    public clojure.asm.tree.analysis.SourceValue merge(final clojure.asm.tree.analysis.SourceValue d, final clojure.asm.tree.analysis.SourceValue w) {
        if (d.insns instanceof SmallSet && w.insns instanceof SmallSet) {
            Set<clojure.asm.tree.AbstractInsnNode> s = ((SmallSet<AbstractInsnNode>) d.insns)
                    .union((SmallSet<AbstractInsnNode>) w.insns);
            if (s == d.insns && d.size == w.size) {
                return d;
            } else {
                return new clojure.asm.tree.analysis.SourceValue(Math.min(d.size, w.size), s);
            }
        }
        if (d.size != w.size || !d.insns.containsAll(w.insns)) {
            HashSet<clojure.asm.tree.AbstractInsnNode> s = new HashSet<clojure.asm.tree.AbstractInsnNode>();
            s.addAll(d.insns);
            s.addAll(w.insns);
            return new clojure.asm.tree.analysis.SourceValue(Math.min(d.size, w.size), s);
        }
        return d;
    }
}
