 [![](https://jitpack.io/v/jomof/kane.svg)](https://jitpack.io/#jomof/kane)

# Introduction
Kane exists at the intersection of conventional data frame libraries like Pandas, formula sheets like Excel, and Kotlin style functional programming. Data and formulas coexist in sheet cells alongside each other. Formulas are written in Kotlin. Kane is designed to be used in a Kotlin Kernel of a Jupyter notebook.

## Quick Start
There are several ways to quickly set up a Jupyter notebook. I'm using [Jetbrains Datalore](https://datalore.jetbrains.com/) to write this introduction.

Once you have a notebook running a Kotlin kernal add a dependency on the Kane library like this.


```kotlin
@file:DependsOn("com.github.jomof:kane:0.1.79")
import com.github.jomof.kane.*
import com.github.jomof.kane.types.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import java.io.File
```

Kane sheets are constructed with the **sheetOf** function:


```kotlin
val sheet = sheetOf {
    val a1 by 1.0
    val a2 by a1 + a1
    add(a2)
}
sheet
```




        A   
      ----- 
    1     1 
    2 A1+A1 



Unlike a Pandas dataframe a Kane sheet can hold formulas in cells not just data. As you can see, the formula A1+A1 is preserved in the sheet cell A2.

You can evaluate the sheet with the eval() function to see the result of A1+A1.


```kotlin
sheet.eval()
```




      A 
      - 
    1 1 
    2 2 



The benefit is that you can change the value of a cell to see it's effect. For example, we can change cell A1 to 2. 


```kotlin
sheet.copy("A1" to 2.0).eval()
```




      A 
      - 
    1 2 
    2 4 



Sheet instances are immutable, so a new sheet was created with the copy(...) function. While the semantics are immutable, copy didn't duplicate any memory unnecessarily. So new sheets are inexpensive to create and destroy. 

Sheet cells support formatting. For example, we can set cell A1 to a value in dollars.


```kotlin
sheet.copy("A1" to "$5.20").eval()
```




         A   
      ------ 
    1  $5.20 
    2 $10.40 



# Other topics
- [Dealing with large .csv files](https://github.com/jomof/kane/blob/main/LargeCsvSupport.md)
