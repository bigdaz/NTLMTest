
What is it
-------------
This is a simple test for NTLM proxy support. It tests accessing http://gradle.org using 3 different mechanisms:

- **java.net.URL** : this is what Gradle Wrapper will use (Milestone 8)
- **Apache HttpClient (built-in NTLM support)** : this is what Gradle-1.0-milestone-7 uses for dependency resolution
- **Apache HttpClient with JCIFS** : this is an alternative that we may switch to in Gradle-1.0-milestone-8.

To run the test
---------------
`./build/install/ntlmtest/bin/ntlmtest <myProxy> <myProxyPort> <myProxyUser> <myProxyPassword>`

If this doesn't work, try adding domain and optional workstation values

`./build/install/ntlmtest/bin/ntlmtest <myProxy> <myProxyPort> <myProxyUser> <myProxyPassword> <myProxyDomain> <myProxyWorkstation>`

To build the app using Gradle
-----------------------------
./gradlew install
