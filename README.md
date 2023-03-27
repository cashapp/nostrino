Nostrino is a Nostr SDK for Kotlin.

[<img src="https://img.shields.io/maven-central/v/app.cash.nostrino/lib.svg?label=latest%20release"/>](https://central.sonatype.com/namespace/app.cash.nostrino)

It is a work in progress, but currently implements the following NIPs.

1, 4, 15, 19, 20, 25

## Getting Started

On the [Sontaype page for Nostrino](https://central.sonatype.com/namespace/app.cash.nostrino), choose the latest version
of `lib` and follow the instructions for inclusion in your build tool.

## Documentation

The API documentation is published with each release
at [https://cashapp.github.io/nostrino](https://cashapp.github.io/nostrino)

## Building

Nostrino uses CashApp's [Hermit](https://cashapp.github.io/hermit/). Hermit ensures that your team, your contributors, 
and your CI have the same consistent tooling. Here are the [installation instructions](https://cashapp.github.io/hermit/usage/get-started/#installing-hermit).

[Activate Hermit](https://cashapp.github.io/hermit/usage/get-started/#activating-an-environment) either
by [enabling the shell hooks](https://cashapp.github.io/hermit/usage/shell/) (one-time only, recommended) or manually 
sourcing the env with `. ./bin/activate-hermit`. 

Use gradle to run all tests

```shell
gradle build
```

## Changelog

See a list of changes in each release in the [CHANGELOG](CHANGELOG.md).

## Contributing

For details on contributing, see the [CONTRIBUTING](CONTRIBUTING.md) guide.
