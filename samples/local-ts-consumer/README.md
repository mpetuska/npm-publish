# Standalone project without kotlin plugin

This project showcases how to setup the plugin to assemble your kotlin project and consume it from a typescript project
locally. No publishing required!

## Things to note

- Sample Kotlin/JS library is in [kt] subdirectory
- The [kt] project is linked to and consumed from [ts] project
    - `yarn relink` task in [ts] project rebuilds [kt] project and reinstalls it for [ts] use
    - `yarn reinstall` task in [ts] project assumes prebuilt [kt] project and only reinstalls it for [ts] use
    - `yarn start` task in [ts] project executes the [ts] app
    - `yarn build` task in [ts] project builds the [ts] app
    - `yarn build:start` task in [ts] project builds the [ts] app and executes it as a plain js file

## Usage

```shell
cd ts
yarn relink
yarn start
```

[kt]: ./kt

[ts]: ./ts