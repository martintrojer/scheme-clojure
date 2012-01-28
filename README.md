# An interpreter of (a subset of) the Scheme programming language

Written in Clojure

Copyright Martin Trojer <martin.trojer@gmail.com>

Licensed under GPLv3

2 versions are available; embedded (internal) and external DSL

For more information see 

* http://martinsprogrammingblog.blogspot.com/2011/11/scheme-as-embedded-dsl-in-clojure.html
* http://martinsprogrammingblog.blogspot.com/2012/01/scheme-as-external-dsl-in-clojure.html

# Embedded DSL

Sources in the folder called

    internal/

## Usage

First compile mtscheme to class files;

    $ mkdir classes
    $ export CLASSPATH=$CLASSPATH:./classes:.
    $ clojure -e "(compile 'mtscheme)"
    
Run the tests

    $ clojure tester.clj

Run the REPL

    $ clojure repl.clj

# External

Leiningen project and sources in the folder called

    external/

## Usage

Run the test suite

    $ lein test

Play with the REPL

    $ lein uberjar
    $ java -jar mtscheme-0.0.1-SNAPSHOT-standalone.jar

    mtscheme 0.1
    nil
    => (+ 1 1)
    2.0

