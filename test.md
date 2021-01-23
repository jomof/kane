```kotlin
@file:DependsOn("com.github.jomof:kane:0.1.59")
%use lets-plot
import com.github.jomof.kane.*
import com.github.jomof.kane.types.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import java.io.File
```


<div id="SVuUNB"></div>
<script type="text/javascript" data-lets-plot-script="library">
    if(!window.letsPlotCallQueue) {
        window.letsPlotCallQueue = [];
    }; 
    window.letsPlotCall = function(f) {
        window.letsPlotCallQueue.push(f);
    };
    (function() {
        var script = document.createElement("script");
        script.type = "text/javascript";
        script.src = "https://dl.bintray.com/jetbrains/lets-plot/lets-plot-1.5.2.min.js";
        script.onload = function() {
            window.letsPlotCall = function(f) {f();};
            window.letsPlotCallQueue.forEach(function(f) {f();});
            window.letsPlotCallQueue = [];


        };
        script.onerror = function(event) {
            window.letsPlotCall = function(f) {};
            window.letsPlotCallQueue = [];
            var div = document.createElement("div");
            div.style.color = 'darkred';
            div.textContent = 'Error loading Lets-Plot JS';
            document.getElementById("SVuUNB").appendChild(div);
        };
        var e = document.getElementById("SVuUNB");
        e.appendChild(script);
    })();
</script>



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
