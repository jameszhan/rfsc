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
Symbol              |   LocalBindingExpr, VarExpr, ConstantExpr, UnresolvedVarExpr
Keyword             |   KeywordExpr
StringExpr          |   StringExpr
empty collection    |   (if (is-meta) MetaExpr EmptyExpr)
IPersistentVector   |   VectorExpr or ConstantExpr
IRecord             |   ConstantExpr
IType               |   ConstantExpr
IPersistentMap      |   MapExpr or ConstantExpr
IPersistentSet      |   SetExpr or ConstantExpr


对于ISeq的处理，首先宏展开(调用macroexpand1)，如果是宏，则重新analyze展开后的结果，如果不是宏，则按照如下方式处理
如果op是fn*，则调用FnExpr进行解析。(fn name [args] body...), (fn name ([args] body...) ([args2] body2...) ...) (fn [args] body...) or (fn ([args] body...) ([args2] body2...) ...)
如果是special form，则调用其专用的parse进行处理，下面使用DefExpr和LetExpr进行分析
否则，InvokeExpr.parse得到InvokeExpr





