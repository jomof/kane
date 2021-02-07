[![](https://jitpack.io/v/jomof/kane.svg)](https://jitpack.io/#jomof/kane)

# Introduction
Kane exists at the intersection of conventional data frame libraries like Pandas, formula sheets like Excel, and Kotlin style functional programming. Data and formulas coexist in sheet cells alongside each other. Formulas are written in Kotlin. Kane is designed to be used in a Kotlin Kernel of a Jupyter notebook.

## Quick Start
There are several ways to quickly set up a Jupyter notebook. I'm using [Jetbrains Datalore](https://datalore.jetbrains.com/) to write this introduction.

Once you have a notebook running a Kotlin kernal add a dependency on the Kane library like this.


```kotlin
@file:DependsOn("com.github.jomof:kane:0.2.30")
import com.github.jomof.kane.*
```

Kane sheets are constructed with the **sheetOf** function:


```kotlin
val sheet = sheetOf {
    val a1 by 1.0
    val a2 by a1 + a1
    listOf(a2)
}
HTML(sheet.html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>A</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>1</td></tr>
    <tr><td>2</td><td>A1+A1</td></tr>
  </tbody>
</table>




Unlike a Pandas dataframe a Kane sheet can hold formulas in cells not just data. As you can see, the formula A1+A1 is preserved in the sheet cell A2.

You can evaluate the sheet with the eval() function to see the result of A1+A1.


```kotlin
HTML(sheet.eval().html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>A</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>1</td></tr>
    <tr><td>2</td><td>2</td></tr>
  </tbody>
</table>




The benefit is that you can change the value of a cell to see it's effect. For example, we can change cell A1 to 2. 


```kotlin
HTML(sheet.copy("A1" to 2.0).eval().html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>A</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>2</td></tr>
    <tr><td>2</td><td>4</td></tr>
  </tbody>
</table>




Sheet instances are immutable, so a new sheet was created with the copy(...) function. While the semantics are immutable, copy didn't duplicate any memory unnecessarily. So new sheets are inexpensive to create and destroy. 

## Formatting

Sheet cells support formatting. For example, we can set cell A1 to a value in dollars.


```kotlin
HTML(sheet.copy("A1" to "$5.20").eval().html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>A</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>$5.20</td></tr>
    <tr><td>2</td><td>$10.40</td></tr>
  </tbody>
</table>




Here's a list of the currently supported data formats.


```kotlin
HTML(Kane.dataFormats.html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>format</th><th>type</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>double</td><td>double</td></tr>
    <tr><td>2</td><td>currency ($1,000)</td><td>dollar</td></tr>
    <tr><td>3</td><td>currency ($1,000.12)</td><td>dollars and cents</td></tr>
    <tr><td>4</td><td>yyyy-MM-dd HH:mm:ss</td><td>date</td></tr>
    <tr><td>5</td><td>yyyy-MM-dd</td><td>date</td></tr>
    <tr><td>6</td><td>string</td><td>String</td></tr>
  </tbody>
</table>




## Plotting

Kane can interoperate with lets_plot with the toMap() function.


```kotlin
%use lets-plot
val rand = java.util.Random(7)
val sheet = sheetOf {
    val rating by List(200) { rand.nextGaussian() } + List(200) { rand.nextGaussian() * 1.5 + 1.5 }
    val cond by List(200) { "A" } + List(200) { "B" }
    listOf(rating, cond)
}
var p = lets_plot(sheet.toMap())
p += geom_density(color="dark_green", alpha=.3) { x="rating"; fill="cond" }
p + ggsize(500, 250)
```

[![](https://jomof.github.io/kane/figures/readme-density.svg)]

# Other topics
- [Dealing with large .csv files](https://github.com/jomof/kane/blob/main/LargeCsvSupport.md)
- [Goal Seeking](https://github.com/jomof/kane/blob/main/GoalSeeking.md)
- Lastly, you can see the autodocs [here](https://github.com/jomof/kane/blob/gh-pages/dokka/-kane/com.github.jomof.kane/index.md)
