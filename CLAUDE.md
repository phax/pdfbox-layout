# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build system

This is a **Gradle** project (not Maven). Use the wrapper (Gradle 8.10.2):

- Build + test: `./gradlew build`
- Compile only: `./gradlew compileJava`
- Regenerate example PDFs in a module: `./gradlew :pdfbox3-layout:createExamples`

Java target is **11** across all modules (`sourceCompatibility = JavaVersion.VERSION_11`).

## Multi-module layout — edit shared `/src`, not the module dirs

Three published artifacts share a single source tree at the repo root:

| module | PDFBox version |
|---|---|
| `pdfbox1-layout` | 1.8.x |
| `pdfbox2-layout` | 2.0.x |
| `pdfbox3-layout` | 3.0.x |

Shared sources live at:

- `/src/main/java/` — library source (edit here)
- `/src/test/java/` — tests
- `/examples/` — example programs, compiled and **executed** during build to produce reference PDFs

The 2.x and 3.x modules adapt imports from the shared source at build time (`adaptToPdfBox2` / `adaptToPdfBox3` filters in their `build.gradle`). Module subdirectories contain only `build.gradle` and a small `compatibility/` shim that overlays a version-specific `CompatibilityHelper.java` on top of the shared one. **Do not duplicate edits into the module dirs** unless you specifically need version-specific behavior — put that in `compatibility/`.

The `pdfbox3-layout` module additionally:
- carries pdfbox3-specific reference PDFs at `test-references/examples/pdf/*.pdf` (overlay onto `src/test/resources/` after the shared copy);
- has a `recordTestReferences` task that re-seeds those references by running the test suite with `-DrecordReferences=true` — each `tearDown` copies the just-produced PDF into the references directory before `deleteOnExit` removes it. Running the recorder out-of-band (in a separate JVM) does **not** reproduce the test JVM's output byte-for-byte (different floating-point accumulation despite identical inputs), so the only reliable way to seed references is from the test itself.

## Code style — does NOT use Hungarian notation

Despite the user's global rules: this project uses plain camelCase (`pdDocument`, `contentStream`), 4-space indent, space before `(` in calls/declarations. Match the surrounding style — do not introduce `m_`, `s_`, `n`/`s`/`a` prefixes here.

License is MIT (see `LICENSE`). Source files have **no per-file license header** — do not add one.

## Tests are visual-diff regressions — and flaky

CI runs `./gradlew build` and uploads `pdfbox{1,2,3}-layout/*.diff.png` as the `test-diffs` artifact. A failing test typically means a rendered PDF page differs from the reference.

`ExampleTest` does pixel-diff comparison at 0.08 color tolerance and is locked to alphabetical order via `@FixMethodOrder(MethodSorters.NAME_ASCENDING)`. `setUp` resets every known piece of mutable static state that examples accumulate — `AnnotationProcessorFactory`, `AnnotationCharacters`, `BaseFont`, `ControlFragment` — otherwise running many example main()s in one JVM produces non-deterministic layouts because each `PDFont` carries a `COSDictionary` that gets bound to the first `PDDocument` that touches it.

`pdfbox1-layout` and `pdfbox2-layout` still set `test.ignoreFailures = true` because their references were generated on Java 8 and Java 21 rendering shifts a few pixels. `pdfbox3-layout` does **not** ignore failures — references there are authoritative.

## PDFBox 3 notes (pdfbox3-layout)

- PDFBox 3 forbids the `cm` (transform) operator inside text objects. The pdfbox3 `CompatibilityHelper.moveTextPosition` takes **absolute** coordinates and emits `Tm` (`setTextMatrix`); the filter rewrites the single call site in `TextLine.java` to pass `x + gap, y - styledText.getBaselineOffset()` (where `x` is the caller's tracked absolute X and the telescoping sum of `baselineDelta` equals `-styledText.getBaselineOffset()`). A naive `newLineAtOffset` substitution loses the prior label/glyph advance — don't go back to it.
- PDFBox 3 dropped the `(PDDocument, PDPage, boolean, boolean)` `PDPageContentStream` constructor; the filter rewrites those to `AppendMode.APPEND`.
- `PDType1Font.HELVETICA` etc. are no longer static fields; the filter rewrites references to `new PDType1Font(Standard14Fonts.FontName.HELVETICA)` and injects the `Standard14Fonts` import.
- `Loader.loadPDF(InputStream)` does not exist; the test's `InputStream` case is wrapped via `RandomAccessReadBuffer`.
- `PDAnnotationTextMarkup` is now abstract; `PDAnnotationHighlight` is the concrete subtype.
- `PDPageContentStream.addLine`/`drawLine` were removed; the filter rewrites the two call sites to `moveTo` + `lineTo` (+ `stroke` for `drawLine`).

## CI / publishing

- CI: `.github/workflows/gradle.yml` (JDK 11 temurin, `./gradlew build` on push/PR to `master`).
- Release publish: `.github/workflows/gradle-publish.yml` runs `gradle publish` to GitHub Packages on release creation; uses `USERNAME` / `TOKEN` env vars.
- Version is set in the root `build.gradle` (`version = '1.0.1'`).

The `org.dm.bundle` (OSGi) plugin was dropped during the Gradle 8 modernization — published JARs no longer carry an OSGi manifest. Re-add if needed.

## Long-term plan

This repo is being retired; "per text formatting" is intended to migrate into `../../git/ph-pdf-layout`. The pdfbox3-layout module is a stopgap to keep this codebase usable on modern PDFBox in the meantime.

## Git etiquette

The user runs all `git commit` and `git push` themselves — stage with specific `git add <path>` and stop. (See `~/.claude/rules/behaviour.md`.)
