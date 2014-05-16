### macroexpand (递归进行宏展开)
~~~java
static Object macroexpand(Object form) {
    Object exf = macroexpand1(form);
    if (exf != form)
        return macroexpand(exf);
    return form;
}
~~~

##### macroexpand1 (form)
~~~lisp
(case (first form)
    isSpecial: form
    isMacro: invoke macro
    handler dot)            ;// (.substring "hello world" 2 5) => (. "hello world" substring 2 5)
                            ;// (clojure.lang.Symbol/intern "a" "b") => (. clojure.lang.Symbol intern "a" "b")
                            ;// (String. "hello") => (new String "hello")
~~~

### eval
1. 宏展开
2. 执行
    1. 如果是do，则逐条递归eval每个cons。
    2.



Var
    isMacro: ^:meta, 标识该function是macro。
    isPublic: ^:private, 只要不是private就是public。
    isDynamic: ^:dynamic, dynamic的var可以被重新绑定。


每一个表达式（例：(+ 1 2)）都会生成一个AFunction的子类。


表达式

quote ->
    LiteralExpr
        ConstantExpr
        NilExpr
        BooleanExpr
        NumberExpr
        StringExpr
        EmptyExpr


form                |   expr
--                  |   --
null                |   NIL_EXPR
true                |   TRUE_EXPR
false               |   FALSE_EXPR
Number              |   (case val (Integer Long Double) NumberExpr ConstantExpr)
Symbol              |
Keyword             |   KeywordExpr
StringExpr          |   StringExpr
empty collection    |   (if (is-meta) MetaExpr EmptyExpr)
ISeq                |
IPersistentVector   |   VectorExpr or ConstantExpr
IRecord             |   ConstantExpr
IType               |   ConstantExpr
IPersistentMap      |   MapExpr or ConstantExpr
IPersistentSet      |   SetExpr or ConstantExpr





