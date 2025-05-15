# jeltz
Java Easy Library for Task automationZ

## Description
jeltz is a Java library designed to simplify the automation of tasks. It provides a set of tools and utilities that make it easier to perform common tasks in Java applications. 

The library is lightweight and easy to use, making it a great choice for developers looking to streamline their workflow.

## Features
- Task automation
- Simple and intuitive Interface
- Lightweight and fast
- Easy integration with existing Java projects

## Installation
To use jeltz in your project, add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.github.jeltz</groupId>
    <artifactId>jeltz-core</artifactId>
    <version>1.0.0</version>
</dependency>
```
If you're using a fat jar (that conains all dependencies), you're all set!

If you're not using a fat jar, you can create a fat jar using the maven-assembly-plugin:

```xml
    <build>
    <plugins>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

The following command creates a jar file with all dependencies included called <jar-name>-jar-with-dependencies.jar.

```bash
mvn clean compile assembly:single
```

Either way, you can run this command to run tasks in  the jar file:
```bash
java -jar target/<jar-name>-jar-with-dependencies.jar com.fairanswers.jeltz.JeltzTaskRunner <taseName> <taskArgs>
````

## Create your own tasks

Here's a simple example of how to use jeltz in your Java application:
1. Add the dependency to your project.
2. Create your own class that extends JeltzTask
3. Fill in the names and description of the task.  Make sure your name doesn't overlap with other tasks.
4. Implement the `run` method to define the task's behavior.

Here's an example of a simple task that prints "Hello, World!" to the console:

```java
import com.fairanswers.jeltz.JeltzTask;

public class HelloWorldTask extends JeltzTask {
    public HelloWorldTask() {
        super("HelloWorldTask", "Prints 'Hello, World!' to the console");
    }

    @Override
    public void run(String[] args) {
        System.out.println("Hello, World!");
    }

    public Set<String> names() {
        // The names of the task, used to identify it in the command line
        // This task can be invoked with "demo" as the command line argument
        // For java version 17 and above, use Set.of("demo");
        Set<String> strings = new java.util.HashSet<>();
        strings.add("hello");
        return strings;
    }

    @Override
    public String description() {
        return "A demonstration task that shows how to use the JeltzTask implementation.";
    }
}
```

Now you can run this task from the command line using the following commands:
```bash
mvn clean compile assembly:single
java -cp target/<jar-name>-jar-with-dependencies.jar com.fairanswers.jeltz.JeltzTaskRunner hello
```
## Options
By default, the options will be 

  verbose = false

  packageName = "com"

Verbose can be turned on with the -v option on the command line between the class name and the task name. For example:
```bash
java -cp target/<jar-name>-jar-with-dependencies.jar com.fairanswers.jeltz.JeltzTaskRunner -v hello
```

To save time, we only scan the classpath for tasks in certain packages.  The packageName options is used to find the task classes that you create. By default, it is set to "com", whcih scans any classes that start with "com".

If you want to change the package name, you can do so by using the -p option on the command line between the class name and the task name. For example:

```bash
java -cp target/<jar-name>-jar-with-dependencies.jar com.fairanswers.jeltz.JeltzTaskRunner -p org.yourname hello
```

