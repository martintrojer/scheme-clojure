# An interpreter of (a subset of) the Scheme programming language.

Written in Clojure

Copyright Martin Trojer <martin.trojer@gmail.com>

Licensed under GPLv3

2 versions are available; internal and external (DSL)

For more information see http://martinsprogrammingblog.blogspot.com/

# Usage

First compile mtscheme to class files;

    $ mkdir classes
    $ export CLASSPATH=$CLASSPATH:./classes:.
    $ clojure -e "(compile 'mtscheme)"
    
Run the tests/repl

    $ clojure tester.clj
    $ clojure repl.clj
