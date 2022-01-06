# NV WebSocket Client & Quarkus & GraalVM Native Image Problems

Quarkus is not a new stuff in the Java world, nor GraalVM. 
GraalVM is capable of creating native executables based on Java classes or .jar files.  
Quarkus is utilizing this feature and letting you creating low footprint and fast startup time based native executables.  

In the near past I was experimenting with two libraries that are using the **nv-websocket-client** lib for integrating WebSockets:
- https://javacord.org/ - https://github.com/Javacord/Javacord
- https://github.com/DV8FromTheWorld/JDA

If any developer would like to integrate Quarkus with any of the mentioned libraries using the "Quarkus way" (you will find this in [that repository]( https://github.com/nandorholozsnyak/nv-websocket-client-quarkus-graalvm)) you would be able to do it, but you will not be able to build it as a native image, because the library is breaking some GraalVM based limitations.

### The problem itself (what I recognized)

Prerequisites to reproduce a problem:
- Java 11 on your computer
- Properly setup GraalVM with GRAALVM_HOME / Docker environment (if GraalVM is not available on your computer Quarkus will use Docker instead)
- Maven (or use the given Maven Wrapper)

Invoke the following command in the project directory: `./mvnw package -Pnative`

After a few minutes you should see the following output:
```text
Error: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: Detected an instance of Random/SplittableRandom class in the image heap. Instances created during image generation have cached seed values and don't behave as expected.  Object has been initialized by the com.neovisionaries.ws.client.Misc class initializer with a trace: 
 	at java.security.SecureRandom.<init>(SecureRandom.java:218)
	at com.neovisionaries.ws.client.Misc.<clinit>(Misc.java:40)
. Try avoiding to initialize the class that caused initialization of the object. The object was probably created by a class initializer and is reachable from a static field. You can request class initialization at image runtime by using the option --initialize-at-run-time=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Detailed message:
Trace: 
	at parsing com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:115)
Call path from entry point to com.neovisionaries.ws.client.Misc.nextBytes(byte[]): 
	at com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:115)
	at com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:128)
	at com.neovisionaries.ws.client.WebSocketOutputStream.write(WebSocketOutputStream.java:48)
	at com.neovisionaries.ws.client.WritingThread.sendFrame(WritingThread.java:486)
	at com.neovisionaries.ws.client.WritingThread.sendFrames(WritingThread.java:360)
	at com.neovisionaries.ws.client.WritingThread.main(WritingThread.java:120)
	at com.neovisionaries.ws.client.WritingThread.runMain(WritingThread.java:55)
	at com.neovisionaries.ws.client.WebSocketThread.run(WebSocketThread.java:45)
	at app//com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:596)
	at app//com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192)
	at com.oracle.svm.core.code.IsolateEnterStub.PosixJavaThreads_pthreadStartRoutine_e1f4a8c0039f8337338252cd8734f63a79b5e3df(generated:0)

com.oracle.svm.core.util.UserError$UserException: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: Detected an instance of Random/SplittableRandom class in the image heap. Instances created during image generation have cached seed values and don't behave as expected.  Object has been initialized by the com.neovisionaries.ws.client.Misc class initializer with a trace: 
 	at java.security.SecureRandom.<init>(SecureRandom.java:218)
	at com.neovisionaries.ws.client.Misc.<clinit>(Misc.java:40)
. Try avoiding to initialize the class that caused initialization of the object. The object was probably created by a class initializer and is reachable from a static field. You can request class initialization at image runtime by using the option --initialize-at-run-time=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Detailed message:
Trace: 
	at parsing com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:115)
Call path from entry point to com.neovisionaries.ws.client.Misc.nextBytes(byte[]): 
	at com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:115)
	at com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:128)
	at com.neovisionaries.ws.client.WebSocketOutputStream.write(WebSocketOutputStream.java:48)
	at com.neovisionaries.ws.client.WritingThread.sendFrame(WritingThread.java:486)
	at com.neovisionaries.ws.client.WritingThread.sendFrames(WritingThread.java:360)
	at com.neovisionaries.ws.client.WritingThread.main(WritingThread.java:120)
	at com.neovisionaries.ws.client.WritingThread.runMain(WritingThread.java:55)
	at com.neovisionaries.ws.client.WebSocketThread.run(WebSocketThread.java:45)
	at app//com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:596)
	at app//com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192)
	at com.oracle.svm.core.code.IsolateEnterStub.PosixJavaThreads_pthreadStartRoutine_e1f4a8c0039f8337338252cd8734f63a79b5e3df(generated:0)

	at com.oracle.svm.core.util.UserError.abort(UserError.java:87)
	at com.oracle.svm.hosted.FallbackFeature.reportAsFallback(FallbackFeature.java:233)
	at com.oracle.svm.hosted.NativeImageGenerator.runPointsToAnalysis(NativeImageGenerator.java:759)
	at com.oracle.svm.hosted.NativeImageGenerator.doRun(NativeImageGenerator.java:529)
	at com.oracle.svm.hosted.NativeImageGenerator.run(NativeImageGenerator.java:488)
	at com.oracle.svm.hosted.NativeImageGeneratorRunner.buildImage(NativeImageGeneratorRunner.java:403)
	at com.oracle.svm.hosted.NativeImageGeneratorRunner.build(NativeImageGeneratorRunner.java:569)
	at com.oracle.svm.hosted.NativeImageGeneratorRunner.main(NativeImageGeneratorRunner.java:122)
	at com.oracle.svm.hosted.NativeImageGeneratorRunner$JDK9Plus.main(NativeImageGeneratorRunner.java:599)
Caused by: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: Detected an instance of Random/SplittableRandom class in the image heap. Instances created during image generation have cached seed values and don't behave as expected.  Object has been initialized by the com.neovisionaries.ws.client.Misc class initializer with a trace: 
 	at java.security.SecureRandom.<init>(SecureRandom.java:218)
	at com.neovisionaries.ws.client.Misc.<clinit>(Misc.java:40)
. Try avoiding to initialize the class that caused initialization of the object. The object was probably created by a class initializer and is reachable from a static field. You can request class initialization at image runtime by using the option --initialize-at-run-time=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Detailed message:
Trace: 
	at parsing com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:115)
Call path from entry point to com.neovisionaries.ws.client.Misc.nextBytes(byte[]): 
	at com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:115)
	at com.neovisionaries.ws.client.Misc.nextBytes(Misc.java:128)
	at com.neovisionaries.ws.client.WebSocketOutputStream.write(WebSocketOutputStream.java:48)
	at com.neovisionaries.ws.client.WritingThread.sendFrame(WritingThread.java:486)
	at com.neovisionaries.ws.client.WritingThread.sendFrames(WritingThread.java:360)
	at com.neovisionaries.ws.client.WritingThread.main(WritingThread.java:120)
 GB	at com.neovisionaries.ws.client.WritingThread.runMain(WritingThread.java:55)
	at com.neovisionaries.ws.client.WebSocketThread.run(WebSocketThread.java:45)
	at app//com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:596)
	at app//com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192)
	at com.oracle.svm.core.code.IsolateEnterStub.PosixJavaThreads_pthreadStartRoutine_e1f4a8c0039f8337338252cd8734f63a79b5e3df(generated:0)

	at com.oracle.graal.pointsto.constraints.UnsupportedFeatures.report(UnsupportedFeatures.java:126)
	at com.oracle.svm.hosted.NativeImageGenerator.runPointsToAnalysis(NativeImageGenerator.java:756)
	... 6 more
Caused by: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: Detected an instance of Random/SplittableRandom class in the image heap. Instances created during image generation have cached seed values and don't behave as expected.  Object has been initialized by the com.neovisionaries.ws.client.Misc class initializer with a trace: 
 	at java.security.SecureRandom.<init>(SecureRandom.java:218)
	at com.neovisionaries.ws.client.Misc.<clinit>(Misc.java:40)
. Try avoiding to initialize the class that caused initialization of the object. The object was probably created by a class initializer and is reachable from a static field. You can request class initialization at image runtime by using the option --initialize-at-run-time=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
	at com.oracle.svm.hosted.image.DisallowedImageHeapObjectFeature.error(DisallowedImageHeapObjectFeature.java:173)
	at com.oracle.svm.core.image.DisallowedImageHeapObjects.check(DisallowedImageHeapObjects.java:65)
	at com.oracle.svm.hosted.image.DisallowedImageHeapObjectFeature.replacer(DisallowedImageHeapObjectFeature.java:149)
	at com.oracle.graal.pointsto.meta.AnalysisUniverse.replaceObject(AnalysisUniverse.java:570)
	at com.oracle.svm.hosted.ameta.AnalysisConstantReflectionProvider.replaceObject(AnalysisConstantReflectionProvider.java:217)
	at com.oracle.svm.hosted.ameta.AnalysisConstantReflectionProvider.interceptValue(AnalysisConstantReflectionProvider.java:188)
	at com.oracle.svm.hosted.ameta.AnalysisConstantReflectionProvider.readValue(AnalysisConstantReflectionProvider.java:102)
	at com.oracle.svm.hosted.ameta.AnalysisConstantReflectionProvider.readFieldValue(AnalysisConstantReflectionProvider.java:81)
	at jdk.internal.vm.compiler/org.graalvm.compiler.nodes.util.ConstantFoldUtil$1.readValue(ConstantFoldUtil.java:51)
	at jdk.internal.vm.compiler/org.graalvm.compiler.core.common.spi.JavaConstantFieldProvider.readConstantField(JavaConstantFieldProvider.java:84)
	at com.oracle.svm.hosted.ameta.AnalysisConstantFieldProvider.readConstantField(AnalysisConstantFieldProvider.java:72)
	at jdk.internal.vm.compiler/org.graalvm.compiler.nodes.util.ConstantFoldUtil.tryConstantFold(ConstantFoldUtil.java:47)
	at com.oracle.svm.hosted.phases.ConstantFoldLoadFieldPlugin.tryConstantFold(ConstantFoldLoadFieldPlugin.java:61)
	at com.oracle.svm.hosted.phases.ConstantFoldLoadFieldPlugin.handleLoadStaticField(ConstantFoldLoadFieldPlugin.java:57)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.genGetStatic(BytecodeParser.java:4944)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.genGetStatic(BytecodeParser.java:4911)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBytecode(BytecodeParser.java:5413)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.iterateBytecodesForBlock(BytecodeParser.java:3477)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.handleBytecodeBlock(BytecodeParser.java:3437)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3282)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.build(BytecodeParser.java:1145)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.buildRootMethod(BytecodeParser.java:1030)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.GraphBuilderPhase$Instance.run(GraphBuilderPhase.java:84)
	at com.oracle.svm.hosted.phases.SharedGraphBuilderPhase.run(SharedGraphBuilderPhase.java:81)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.Phase.run(Phase.java:49)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.BasePhase.apply(BasePhase.java:212)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.Phase.apply(Phase.java:42)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.Phase.apply(Phase.java:38)
	at com.oracle.graal.pointsto.flow.AnalysisParsedGraph.parseBytecode(AnalysisParsedGraph.java:132)
	at com.oracle.graal.pointsto.meta.AnalysisMethod.ensureGraphParsed(AnalysisMethod.java:621)
	at com.oracle.graal.pointsto.flow.MethodTypeFlowBuilder.parse(MethodTypeFlowBuilder.java:163)
	at com.oracle.graal.pointsto.flow.MethodTypeFlowBuilder.apply(MethodTypeFlowBuilder.java:321)
	at com.oracle.graal.pointsto.flow.MethodTypeFlow.createTypeFlow(MethodTypeFlow.java:293)
	at com.oracle.graal.pointsto.flow.MethodTypeFlow.ensureTypeFlowCreated(MethodTypeFlow.java:282)
	at com.oracle.graal.pointsto.flow.MethodTypeFlow.addContext(MethodTypeFlow.java:103)
	at com.oracle.graal.pointsto.flow.StaticInvokeTypeFlow.update(InvokeTypeFlow.java:420)
	at com.oracle.graal.pointsto.PointsToAnalysis$2.run(PointsToAnalysis.java:595)
	at com.oracle.graal.pointsto.util.CompletionExecutor.executeCommand(CompletionExecutor.java:188)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$executeService$0(CompletionExecutor.java:172)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool.awaitQuiescence(ForkJoinPool.java:2984)
	at com.oracle.graal.pointsto.util.CompletionExecutor.complete(CompletionExecutor.java:238)
	at com.oracle.graal.pointsto.PointsToAnalysis.checkObjectGraph(PointsToAnalysis.java:680)
	at com.oracle.graal.pointsto.PointsToAnalysis.finish(PointsToAnalysis.java:644)
	at com.oracle.svm.hosted.NativeImageGenerator.runPointsToAnalysis(NativeImageGenerator.java:704)
	... 6 more
Error: Image build request failed with exit status 1
```

The most interesting stuff is this:
```
Caused by: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: Detected an instance of Random/SplittableRandom class in the image heap. Instances created during image generation have cached seed values and don't behave as expected.  Object has been initialized by the com.neovisionaries.ws.client.Misc class initializer with a trace: 
 	at java.security.SecureRandom.<init>(SecureRandom.java:218)
	at com.neovisionaries.ws.client.Misc.<clinit>(Misc.java:40)
```

The `com.neovisionaries.ws.client.Misc` class is initiating (`com.neovisionaries.ws.client.Misc.sRandom`) an instance of the `java.security.SecureRandom` class and because of that GraalVM native image build fails, this is a limitation of GraalVM. That during build time this can not happen (https://www.graalvm.org/reference-manual/native-image/JCASecurityServices/), so any the following flag for the compiler will not make any difference in the result, only in the error message:` --initialize-at-run-time=com.neovisionaries.ws.client.Misc`.  
Quarkus is basically, because its augmentation process, initiating a lot of things at build time, trying to enhance most of the code and at native image build phase the whole process fails because the connection is being setup and it breaks. I think the whole flow can be visualized with the following call chain:
1. `com.neovisionaries.ws.client.WebSocket.connect()`
2. `com.neovisionaries.ws.client.WebSocket.shakeHands`
3. `com.neovisionaries.ws.client.WebSocket.generateWebSocketKey`
4. `com.neovisionaries.ws.client.Misc.nextBytes(byte[])`

### A possible solution

I do not really have a proper vision why in the `com.neovisionaries.ws.client.Misc` class the sRandom field is initialized there (but I think it is a good way for SecureRandom), it is being used only one place in the `com.neovisionaries.ws.client.Misc.nextBytes(byte[])` method, but a possible fix would just change this SecureRandom initiation and would be placed in the method itself. So:

``` java
class Misc {
     //private static final SecureRandom sRandom = new SecureRandom();
     ...
     /**
     * Fill the given buffer with random bytes.
     */
    public static byte[] nextBytes(byte[] buffer)
    {
        new SecureRandom().nextBytes(buffer);

        return buffer;
    }
    ...
    }
```

Or if it is a really bad one then maybe the following:
```java
/**
 * WebSocket Security.
 */
public final class Security {

    private final SecureRandom sRandom;

    private static Security instance;

    private Security() {
        sRandom = new SecureRandom();
    }

    /**
     * Returns the security instance.
     *
     * @return security instance.
     */
    public static Security getInstance() {
        if (instance == null) {
            instance = new Security();
        }
        return instance;
    }

    /**
     * Fill the given buffer with random bytes.
     */
    public byte[] nextBytes(byte[] buffer) {
        sRandom.nextBytes(buffer);
        return buffer;
    }

    /**
     * Create a buffer of the given size filled with random bytes.
     */
    public byte[] nextBytes(int nBytes) {
        byte[] buffer = new byte[nBytes];
        return nextBytes(buffer);
    }

}
```

And replace the Misc references in the code:
```java
public class WebSocket {
    ....
    private static String generateWebSocketKey()
    {
        // "16-byte value"
        byte[] data = new byte[16];

        // "randomly selected"
        //  Misc.nextBytes(data);
        Security.getInstance().nextBytes(data);

        // "base64-encoded"
        return Base64.encode(data);
    }
} 

public class WebSocketOutputStream {
    ...
    public void write(WebSocketFrame frame) throws IOException
    {
        writeFrame0(frame);
        writeFrame1(frame);
        writeFrameExtendedPayloadLength(frame);

        // Generate a random masking key.
        //  byte[] maskingKey = Misc.nextBytes(4);
        byte[] maskingKey = Security.getInstance().nextBytes(4);

        // Write the masking key.
        write(maskingKey);

        // Write the payload.
        writeFramePayload(frame, maskingKey);
    }
}

```

With these changes I think that the functionality does not get hurt and after these changes this project would also work in native mode too, not only this project but a project that is based on the mentioned JavaCord and JDA using the same dependencies and CDI way (@Produces) to create a connection to the specific websocket server.

### Motivation behind the change
I'm planning to create Quarkus extensions for these two mentioned libraries and in order to make sure they could be compiled to native binaries this change is a must right now. Of course, native image mode is not a "must have" for Quarkus extension, but the framework itself was built around utilizing the native image generation.


# quarkus-nv-websocket-dependency Project - Basic Quarkus README.MD

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory. Be aware that it’s not an _über-jar_ as
the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quarkus-nv-websocket-dependency-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.