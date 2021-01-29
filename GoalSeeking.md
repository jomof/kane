# Goal Seeking
Because Kane sheets can contain formulas in addition to plain data, it's possible to seek a goal. You can minimize a cell in the sheet by changing other cells that you choose. This is done with the minimize(...) function.

Under the covers, Kane uses gradient descent to try to reach the goal so the formulas need to be differentiable.

This walk-through will show a very simple case of using minimize(...).

Topics covered:
- Using sheetOfCsv(...) to construct a sheet from a string in CSV format.
- Adding rows, columns, and cells to an existing sheet.
- Using minimize(...) to seek a goal


```kotlin
@file:DependsOn("com.github.jomof:kane:0.1.90")
import com.github.jomof.kane.*
import com.github.jomof.kane.types.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import java.io.File

```


```kotlin
val raw = sheetOfCsv {
    """
    A,B
    1.0,-0.5
    2.0,-1.0
    3.0,-1.5
    4.0,-2.0
    """
}
HTML(raw.html)

```

    Read 4 rows with 2 columns






<table id="table_id" class="display">
<thead><tr>
  <th/><th>A</th><th>B</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>1</td><td>-0.5</td></tr>
    <tr><td>2</td><td>2</td><td>-1</td></tr>
    <tr><td>3</td><td>3</td><td>-1.5</td></tr>
    <tr><td>4</td><td>4</td><td>-2</td></tr>
  </tbody>
</table>




## Add an error function to the sheet

We'd like to know if the data in column A is related to column B linearly. That is, what is the best y=mx+b relationship?

Let's define 'm' and 'b' with starting values of 0. These are the change variables that will be modified in order to minimize the error function we're about to create.

Next, we'll define 'prediction' which is column A times m plus b. I've called column A 'x' to increase readability.

After that, we'll define 'error' which is the per row error function. The function needs to be differentiable and it needs to be positive or zero since we're going to minimize it. I chose (prediction - actual)².

Lastly, we'll define 'totalError' which is the sum of all of the 'error' rows.


```kotlin
val error = raw.copy {
    val x by range("A")
    val actual by range("B")
    val m by 0.0
    val b by 0.0
    val prediction by m * x + b
    val error by pow(prediction - actual, 2.0)
    val totalError by sum(error)
    listOf(totalError)
}
HTML(sheet.html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>x</th><th>actual</th><th>prediction</th><th>error</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>1</td><td>-0.5</td><td>m*A1+b</td><td>(m*A1+b-B1)²</td></tr>
    <tr><td>2</td><td>2</td><td>-1</td><td>m*A2+b</td><td>(m*A2+b-B2)²</td></tr>
    <tr><td>3</td><td>3</td><td>-1.5</td><td>m*A3+b</td><td>(m*A3+b-B3)²</td></tr>
    <tr><td>4</td><td>4</td><td>-2</td><td>m*A4+b</td><td>(m*A4+b-B4)²</td></tr>
  </tbody>
</table>

b=0<br/>
m=0<br/>
totalError=sum((m*A+b-B)²)<br/>



The formulas all look good. Let's evaluate the sheet to see the starting error.


```kotlin
HTML(error.eval().html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>x</th><th>actual</th><th>prediction</th><th>error</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>1</td><td>-0.5</td><td>0</td><td>0.25</td></tr>
    <tr><td>2</td><td>2</td><td>-1</td><td>0</td><td>1</td></tr>
    <tr><td>3</td><td>3</td><td>-1.5</td><td>0</td><td>2.25</td></tr>
    <tr><td>4</td><td>4</td><td>-2</td><td>0</td><td>4</td></tr>
  </tbody>
</table>

b=0<br/>
m=0<br/>
totalError=7.5<br/>



So the total error is 7.5 when m=0 and b=0.

## Call minimize(...)

Now, let's minimize 'totalError' by changing the values of 'm' and 'b'


```kotlin
val minimized = error.minimize("totalError", listOf("m", "b"))
HTML(minimized.html)
```

    Expanding target expression totalError
    Differentiating target expression totalError with respect to change variables: m b
    Plan has 37 common sub-expressions and uses 296 bytes
    Minimizing
    Target 'totalError' reduced from 7.5 to 0






<table id="table_id" class="display">
<thead><tr>
  <th/><th>x</th><th>actual</th><th>prediction</th><th>error</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>1</td><td>-0.5</td><td>m*A1+b</td><td>(m*A1+b-B1)²</td></tr>
    <tr><td>2</td><td>2</td><td>-1</td><td>m*A2+b</td><td>(m*A2+b-B2)²</td></tr>
    <tr><td>3</td><td>3</td><td>-1.5</td><td>m*A3+b</td><td>(m*A3+b-B3)²</td></tr>
    <tr><td>4</td><td>4</td><td>-2</td><td>m*A4+b</td><td>(m*A4+b-B4)²</td></tr>
  </tbody>
</table>

b=0<br/>
m=-0.5<br/>
totalError=sum((m*A+b-B)²)<br/>



## All done
Okay, the sheet was minimized. Notice that 'm' is now set to -0.5 and 'b' was left at 0.


```kotlin
HTML(minimized.eval().html)
```





<table id="table_id" class="display">
<thead><tr>
  <th/><th>x</th><th>actual</th><th>prediction</th><th>error</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>1</td><td>-0.5</td><td>-0.5</td><td>0</td></tr>
    <tr><td>2</td><td>2</td><td>-1</td><td>-1</td><td>0</td></tr>
    <tr><td>3</td><td>3</td><td>-1.5</td><td>-1.5</td><td>0</td></tr>
    <tr><td>4</td><td>4</td><td>-2</td><td>-2</td><td>0</td></tr>
  </tbody>
</table>

b=0<br/>
m=-0.5<br/>
totalError=0<br/>




```kotlin

```
