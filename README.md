# DownloadDemo
App download demo with AsnycTask on Android Studio.

![image](https://github.com/SenCoder/DownloadDemo/blob/master/states.png)

```java
public enum Status {
        /* before start */
        PENDING,
        /* downloading */
        RUNNING,
        /* pause button press */
        PAUSED,
        /* finish downloading */
        FINISHED,
        /* apk installed */
        INSTALLED,
    }
```
