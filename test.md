```kotlin
@file:DependsOn("com.github.jomof:kane:0.1.59")
import com.github.jomof.kane.*
import com.github.jomof.kane.types.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import java.io.File
```


```kotlin
val sheet = sheetOf {
    val a1 by 0.0
    val b1 by a1 + a1
    add(b1)
}
sheet
```




      A   B   
      - ----- 
    1 0 A1+A1 




```kotlin

```


```kotlin

```
