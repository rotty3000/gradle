
## New and noteworthy

Here are the new features introduced in this Gradle release.

<!--
IMPORTANT: if this is a patch release, ensure that a prominent link is included in the foreword to all releases of the same minor stream.
Add-->

### More work avoidance when using `@Classpath` task properties

For built-in and custom tasks that use the `@Classpath` annotation, Gradle now performs deeper inspection of the classpath to filter out some differences that do not affect task execution.  Gradle will ignore changes to timestamps within a jar file and the order of entries inside a jar file.
 
In previous versions, for tasks like `Javadoc`, `Checkstyle` and `Test`, Gradle would consider the task out-of-date if the content of the classpath changed in any way (order of classes in a jar, timestamps of class files, etc). 

### Extensions now have a public type

Extensions can now be registered in `ExtensionContainer`s with an explicit public type.
 This allows plugin authors to hide their implementation type from build scripts and
 allow `ExtensionContainer`s to expose a schema of all the registered extensions.

For example, if you have a `FancyExtension` type, implemented by some `DefaultFancyExtension` type, here is how
 you should register it:

    // If you want to delegate the extension instance creation to Gradle:
    project.extensions.create FancyExtension, 'fancy', DefaultFancyExtension

    // Or if you need to create the extension instance yourself:
    FancyExtension fancyInstance = new DefaultFancyExtension(...)
    project.extensions.add FancyExtension, 'fancy', fancyInstance

### BuildActionExecutor supports running tasks

Tooling API clients can now run tasks before running a build action. This allows them to fetch tooling models which depend on the result of
executing some task. This mirrors the existing `ModelBuilder.forTasks()` API.

### Support for multi-value Javadoc options

Gradle has added support for command-line options to doclets that can appear [multiple times and have multiple values](javadoc/org/gradle/external/javadoc/CoreJavadocOptions.html#addMultilineMultiValueOption-java.lang.String-).

In previous versions of Gradle, it was not possible to supply command-line options like:

    -myoption 'foo' 'bar'
    -myoption 'baz'
    
Gradle would produce a single `-myoption` or combine the option's value into a single argument.

    javadoc {
        options {
            def myoption = addMultilineMultiValueOption("myoption")
            myoption.setValue([
                [ "foo", "bar" ],
                [ "baz" ]
            ])
        }
    }

### Plugin resolution rules

Gradle now allows you to adjust how plugins are resolved by providing plugin resolution rules. For instance, you could
specify a default version for a plugin so you don't have to repeat it in every project. Or you could tell Gradle what implementation artifact it should
look for in case the plugin is not published with plugin markers.

   pluginManagement {
     repositories {
       maven { url = 'someUrl'}
     }
     resolutionStrategy {
       eachPlugin {
         if (requested.id.namespace = 'my.plugins') {
            useTarget {
                version = '1.3'
            }
         }
       }
     }
   }

The `pluginManagement` block supersedes the existing `pluginRepositories` block. Moreover, you now have full access to the `Settings` DSL inside that block,
so you can make decisions e.g. based on start parameters. You can also configure plugin management from an init script by using the `settingsEvaluated {}` hook.

<!--
### Example new and noteworthy
-->

## Promoted features

Promoted features are features that were incubating in previous versions of Gradle but are now supported and subject to backwards compatibility.
See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the features that have been promoted in this Gradle release.

<!--
### Example promoted
-->

## Fixed issues

## Deprecations

Features that have become superseded or irrelevant due to the natural evolution of Gradle become *deprecated*, and scheduled to be removed
in the next major Gradle version (Gradle 4.0). See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the newly deprecated items in this Gradle release. If you have concerns about a deprecation, please raise it via the [Gradle Forums](https://discuss.gradle.org).

### pluginRepositories block superseded

The `pluginRepositories` method in `settings.gradle` is superseded by the new `pluginManagement.repositories` method.

<!--
### Example deprecation
-->

## Potential breaking changes

### Core extensions should be addressed by their public type

Now that extensions implementation type is hidden from plugins and build scripts that extensions can only be
 addressed by their public type, some Gradle core extensions are not addressable by their implementation type anymore:

- `DefaultExtraPropertiesExtension`, use `ExtraPropertiesExtension` instead
- `DefaultDistributionContainer`, use `DistributionContainer` instead
- `DefaultPublishingExtension`, use `PublishingExtension` instead
- `DefaultPlatformContainer`, use `PlatformContainer` instead
- `DefaultBuildTypeContainer`, use `BuildTypeContainer` instead
- `DefaultFlavorContainer`, use `FlavorContainer` instead
- `DefaultNativeToolChainRegistry`, use `NativeToolChainRegistry` instead

<!--
### Example breaking change
-->

### Configurations can be unresolvable

Since Gradle 3.3, configurations can be marked as not resolvable. If you or a plugin tries to resolve such a configuration, an `IllegalStateException` will be thrown. You can check whether a configuration is resolvable by calling `Configuration#isCanBeResolved()`. A configuration that cannot be resolved has a special meaning: it's often only there to declare dependencies only.

Although the concept had already been introduced in Gradle 3.3, the first release that comes with unresolvable configurations by default is Gradle 3.4. The Java and Java Library plugins add the following unresolvable configurations: ```"apiElements", "implementation", "runtimeElements", "runtimeOnly", "testImplementation", "testRuntimeOnly"```. The concept has been introduced in order to support variant aware dependency resolution.

## External contributions

We would like to thank the following community members for making contributions to this release of Gradle.

<!--
 - [Some person](https://github.com/some-person) - fixed some issue (GRADLE-1234)
-->
 - [Attila Kelemen](https://github.com/kelemen) - Project.file supports java.nio.file.Path instances ([gradle/gradle#813](https://github.com/gradle/gradle/pull/813))
 - [Kevin Page](https://github.com/kpage) - Eclipse resource filters ([gradle/gradle#846](https://github.com/gradle/gradle/pull/846))
 - [Jacob Beardsley](https://github.com/jacobwu) - Fix `NullPointerException` when excluding transitive dependencies in dependency configuration ([gradle/gradle#1113](https://github.com/gradle/gradle/pull/1113))
 - [Eitan Adler](https://github.com/grimreaper) - Minor tests cleanup ([gradle/gradle#1219](https://github.com/gradle/gradle/pull/1219))
 - [Vladislav Soroka](https://github.com/vladsoroka) - Allow environment variables to be configured through Tooling API ([gradle/gradle#1029](https://github.com/gradle/gradle/pull/1029))
 - [Björn Kautler](https://github.com/Vampire) - Update user guide for build comparison about supported builds ([gradle/gradle#1266](https://github.com/gradle/gradle/pull/1266))
 - [Joshua Street](https://github.com/jjstreet) - Align usage of `groovy-all` dependency across multiple example in user guide ([gradle/gradle#1446](https://github.com/gradle/gradle/pull/1446))
 - [Lucas Smaira](https://github.com/lsmaira) - BuildActionExecutor.forTasks() support ([gradle/gradle#1442](https://github.com/gradle/gradle/pull/1442))
 - [Ethan Hall](https://github.com/ethankhall) - Plugin resolution rules ([gradle/gradle#1343](https://github.com/gradle/gradle/pull/1343))

We love getting contributions from the Gradle community. For information on contributing, please see [gradle.org/contribute](https://gradle.org/contribute).

## Known issues

Known issues are problems that were discovered post release that are directly related to changes made in this release.
