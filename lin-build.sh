#! /bin/sh

me=`basename $0`

die() {
    echo >&2 $me: $*
    exit 1
}

build_dir=build
source_dir=src

mkdir -p $build_dir || die "unable to create build dir: $build_dir"

find $source_dir -type f | egrep '\.java$' | CLASSPATH=$source_dir:lib/java_cup_runtime.jar xargs javac -encoding latin1 -d $build_dir
