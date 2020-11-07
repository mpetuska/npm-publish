## Changelog

### Breaking Changes
* peer and optional npm dependencies priorities switched. 
  Optional dependency is not top priority, putting peer to 2nd place.

### New Features
* `shrinkwrapBundledDependencies` option added to publication and global configs to 
  bypass old npm [bug](https://github.com/npm/npm/issues/6435). Note that this only works for npm and not 
  yarn (since yarn just ignores npm-shrinkwrap.json).

### Fixes
N/A
