# An interpreter of (a subset of) the Scheme programming language

Written in Clojure

Copyright Martin Trojer <martin.trojer@gmail.com>

Licensed under GPLv3

This is a toy, created as code example to the blogposts listed below.
It was never the intention create a complete Scheme implementation.
If you're interested in Scheme on the JVM, I suggest Kawa
http://www.gnu.org/software/kawa/

2 versions are available; embedded (internal) and external DSL

For more information see

* http://martintrojer.github.io/clojure/2011/11/29/scheme-as-an-embedded-dsl-in-clojure/
* http://martintrojer.github.io/clojure/2012/01/28/scheme-as-an-external-dsl-in-clojure/

# Embedded DSL

Sources in the folder called

    internal/

## Usage

    $ lein run

# External

Leiningen project and sources in the folder called

    external/

## Usage

Run the test suite

    $ lein test

Play with the REPL

    $ lein run
