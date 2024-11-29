# DEV Notes

## How to release a new version

Before create a new version please update the CHANGELOG.md.

```bash
bash dev/scripts/release.sh 1.0.0
```

This will trigger a CI/CD pipeline where it will:

* Build & packaging the JDK library and CLI
* Build & deploy a docker image
* Create a draft GitHub release

After the pipeline ends please do the following step:

* Update the draft GitHub release
* Run the prepare next version script

```bash
bash dev/scripts/prepare-next-release.sh 1.1.0
```
