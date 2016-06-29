#! /bin/sh

me=`basename $0`

die() {
    echo >&2 $me: $*
    exit 1
}

build_dir=build
[ -d $build_dir ] || die "unable to read build dir: $build_dir"

user_items_dir=rec/usu/per
mkdir -p $user_items_dir
[ -d $user_items_dir ] || die "unable to read user items dir: $build_dir"

CLASSPATH=$build_dir:lib/java_cup_runtime.jar java jmud.JMud
