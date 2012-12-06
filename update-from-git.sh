#!/usr/bin/env bash
projectdir="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$projectdir"

branch="$(git rev-parse --abbrev-ref HEAD)"

if [[ "$branch" -ne "master" ]]; then
  echo "You must be on the master branch before running this script."
  echo "Commit or stash any pending changes and run:"
  echo
  echo "git checkout master"
  echo
  echo "and try again. Then you can rebase any new commits by doing:"
  echo
  echo "git checkout $branch && git rebase master"
  echo
  exit 1
fi

git pull origin master
git submodule update --init
