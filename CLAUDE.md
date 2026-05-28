# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build system

Plain Maven. The parent pom inherits from `com.helger:parent-pom:3.0.3`, which supplies Java compile target and standard plugin versions.

- Build + test: `mvn -B verify`
- Compile only: `mvn -B compile`
- Publish to GitHub Packages: `mvn -B deploy` (CI does this on release)

## Multi-module layout — each module is independent

| module | PDFBox version | groupId |
|---|---|---|
| `pdfbox1-layout` | 1.8.x | `com.helger` (via parent) |
| `pdfbox2-layout` | 2.0.x | `com.helger` |
| `pdfbox3-layout` | 3.0.x | `com.helger` |

Each module is a standard Maven jar with its own `src/main/java`, `src/test/java`, `src/test/resources` — no shared source tree, no build-time source transformation, no gmaven-plus, no copying. **A change to the pdfbox 1.x copy of `TextLine.java` does NOT propagate to pdfbox 2.x or 3.x — apply it in all three places by hand.**

The three trees diverge along PDFBox API breaks: pdfbox2/3 use the relocated `PDPageContentStream` import, pdfbox3 uses `Standard14Fonts.FontName.*` instead of static `PDType1Font.HELVETICA`, `Loader.loadPDF` instead of `PDDocument.load`, `AppendMode.APPEND` instead of `(true, true)`, etc. The pdfbox3 `CompatibilityHelper` and `BaseFont`/`ControlFragment` shims are inlined directly into the module's `src/main/java` (no `compatibility/` overlay anymore).

## Code style — does NOT use Hungarian notation

Despite the user's global rules: this project uses plain camelCase (`pdDocument`, `contentStream`), 4-space indent, space before `(` in calls/declarations. Match the surrounding style — do not introduce `m_`, `s_`, `n`/`s`/`a` prefixes here.

License is MIT (see `LICENSE`). Source files have **no per-file license header** — do not add one.

## Tests are visual-diff regressions — and flaky

CI runs `mvn -B verify` and uploads `pdfbox{1,2,3}-layout/*.diff.png` as the `test-diffs` artifact. A failing test typically means a rendered PDF page differs from the reference PDF at `src/test/resources/examples/pdf/*.pdf`.

`ExampleTest` does pixel-diff comparison at 0.08 color tolerance and is locked to alphabetical order via `@FixMethodOrder(MethodSorters.NAME_ASCENDING)`. `setUp` resets every known piece of mutable static state that examples accumulate — `AnnotationProcessorFactory`, `AnnotationCharacters`, `BaseFont`, `ControlFragment` — otherwise running many example main()s in one JVM produces non-deterministic layouts because each `PDFont` carries a `COSDictionary` that gets bound to the first `PDDocument` that touches it.

All three modules set `<testFailureIgnore>true</testFailureIgnore>` on surefire because PDF byte output is JVM-startup-dependent (floating-point accumulation in font metrics) and CI rendering can shift a few pixels between JDK versions. As of the Maven cutover the per-module reference PDFs match surefire's output: pdfbox1 and pdfbox3 pass 16/16, pdfbox2 fails `testCustomAnnotation` and `testLinks` (same flake that was visible during the Gradle build on the JDK21-shifted Java 8 baseline). Failures surface via the `*.diff.png` artifacts uploaded by CI rather than failing the build.

## PDFBox 3 notes (pdfbox3-layout)

The three module trees diverge most heavily at these spots — if you back-port a fix from pdfbox3 to pdfbox1/2 (or vice-versa) you'll have to translate by hand.

- PDFBox 3 forbids the `cm` (transform) operator inside text objects. The pdfbox3 `CompatibilityHelper.moveTextPosition` takes **absolute** coordinates and emits `Tm` (`setTextMatrix`); the single call site in `pdfbox3-layout/src/main/java/rst/pdfbox/layout/text/TextLine.java` passes `x + gap, y - styledText.getBaselineOffset()` (where `x` is the caller's tracked absolute X and the telescoping sum of `baselineDelta` equals `-styledText.getBaselineOffset()`). A naive `newLineAtOffset` substitution loses the prior label/glyph advance — don't go back to it.
- PDFBox 3 dropped the `(PDDocument, PDPage, boolean, boolean)` `PDPageContentStream` constructor; pdfbox3 uses `AppendMode.APPEND` everywhere.
- `PDType1Font.HELVETICA` etc. are no longer static fields; pdfbox3 constructs `new PDType1Font(Standard14Fonts.FontName.HELVETICA)`.
- `Loader.loadPDF(InputStream)` does not exist; pdfbox3's `ExampleTest` wraps the `InputStream` via `RandomAccessReadBuffer`.
- `PDAnnotationTextMarkup` is now abstract; pdfbox3's `CustomAnnotation` uses the concrete `PDAnnotationHighlight`.
- `PDPageContentStream.addLine`/`drawLine` were removed; pdfbox3's `HorizontalRuler.java` and `UnderlineAnnotationProcessor.java` use `moveTo` + `lineTo` (+ `stroke` for `drawLine`).

## CI / publishing

- CI: `.github/workflows/maven.yml` (JDK 11 temurin, `mvn -B verify` on push/PR to `master`).
- Release publish: `.github/workflows/maven-publish.yml` runs `mvn deploy` to GitHub Packages on release creation; uses `USERNAME` / `TOKEN` env vars wired into the `github` server id (see `<distributionManagement>` in the root pom).
- Version is set in the root `pom.xml` (`<version>1.0.1</version>`).

The `org.dm.bundle` (OSGi) plugin was dropped during the Gradle 8 modernization and not re-added — published JARs do not carry an OSGi manifest. Re-add via `maven-bundle-plugin` if needed.

## Long-term plan

This repo is being retired; "per text formatting" is intended to migrate into `../../git/ph-pdf-layout`. The pdfbox3-layout module is a stopgap to keep this codebase usable on modern PDFBox in the meantime.

## Git etiquette

The user runs all `git commit` and `git push` themselves — stage with specific `git add <path>` and stop. (See `~/.claude/rules/behaviour.md`.)
