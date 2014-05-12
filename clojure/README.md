### Background
This module is depends on Clojure(version 1.6) + ASM(version 4.1)


### How to run it
~~~shell
java clojure.main
~~~

## Code Analyse

### Eval

1. macroexpand
2. do eval
    (case type
        ISeq:
        IType:
    3. others

### Forms
#####Special From

##### &
~~~lisp
hello
~~~

##### .

~~~lisp
(. clojure.lang.RT (cons 1 '(2 3)))
(. clojure.lang.RT cons 1 '(2 3))
(clojure.lang.RT/cons 1 '(2 3))
~~~

quote
def
var
if
set!
fn*
let*
letfn*
case*
loop*
do
recur
try
throw
catch
finally
reify*
deftype*
new
monitor-enter
monitor-exit
clojure.core/import*





















