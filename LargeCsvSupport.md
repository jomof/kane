# Dealing with large .csv files
When a CSV file is very large it can be useful to reduce it to a useful subset. Let's look at a table of US COVID-19 hospital statistics (found at data.gov). This data is pretty large, so let's first read a 2% sample to see what's in there.


```kotlin
@file:DependsOn("com.github.jomof:kane:0.1.79")
import com.github.jomof.kane.*
import com.github.jomof.kane.impl.types.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.impl.sheet.*
import java.io.File

```


```kotlin
val peek = readCsv("data/covid.csv", sample = 0.02)
```

    Sampled 1706 of 87369 rows with 93 columns


As you can see, we sampled around 1700 rows out of 87,000+ and there are 93 columns.

We can get a more readable list of columns with the 'types' field on the sheet.


```kotlin
HTML(peek.types.head(15).html)
```


<table id="table_id" class="display" style="width:100%">
<thead><tr>
  <th/><th>name</th><th>type</th><th>format</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>hospital_pk</td><td>String</td><td>string</td></tr>
    <tr><td>2</td><td>collection_week</td><td>date</td><td>yyyy-MM-dd</td></tr>
    <tr><td>3</td><td>state</td><td>String</td><td>string</td></tr>
    <tr><td>4</td><td>ccn</td><td>String</td><td>string</td></tr>
    <tr><td>5</td><td>hospital_name</td><td>String</td><td>string</td></tr>
    <tr><td>6</td><td>address</td><td>String</td><td>string</td></tr>
    <tr><td>7</td><td>city</td><td>String</td><td>string</td></tr>
    <tr><td>8</td><td>zip</td><td>double</td><td>double</td></tr>
    <tr><td>9</td><td>hospital_subtype</td><td>String</td><td>string</td></tr>
    <tr><td>10</td><td>fips_code</td><td>double</td><td>double</td></tr>
    <tr><td>11</td><td>is_metro_micro</td><td>String</td><td>string</td></tr>
    <tr><td>12</td><td>total_beds_7_day_avg</td><td>double</td><td>double</td></tr>
    <tr><td>13</td><td>all_adult_hospital_beds_7_day_avg</td><td>double</td><td>double</td></tr>
    <tr><td>14</td><td>all_adult_hospital_inpatient_beds_7_day_avg</td><td>double</td><td>double</td></tr>
    <tr><td>15</td><td>inpatient_beds_used_7_day_avg</td><td>double</td><td>double</td></tr>
  </tbody>
</table>




Let's keep all of the rows, but filter down to just a few interesting columns.


```kotlin
val filtered = readCsv(
    "data/covid.csv",
    keep = listOf("collection_week", "hospital_name", "all_adult_hospital_inpatient_bed_occupied_7_day_avg")
)
HTML(filtered.head(10).html)
```

    Read 87369 rows with 3 columns

<table id="table_id" class="display" style="width:100%">
<thead><tr>
  <th/><th>collection_week</th><th>hospital_name</th><th>all_adult_hospital_inpatient_bed_occupied_7_day_avg</th></thead></tr>
  <tbody>
    <tr><td>1</td><td>2020-11-27</td><td>Crescent City Surgical Centre</td><td>-999999</td></tr>
    <tr><td>2</td><td>2020-11-27</td><td>CDT Susana Centeno</td><td>-999999</td></tr>
    <tr><td>3</td><td>2020-11-27</td><td>Hospital Industrial C.F.S.E</td><td>8.7</td></tr>
    <tr><td>4</td><td>2020-11-27</td><td>Alexandria Emergency Hospital</td><td>-999999</td></tr>
    <tr><td>5</td><td>2020-11-27</td><td>Elite Medical Center</td><td>-999999</td></tr>
    <tr><td>6</td><td>2020-11-27</td><td>HealthproMed</td><td>-999999</td></tr>
    <tr><td>7</td><td>2020-11-27</td><td>El Paso LTAC Hospital</td><td>20.4</td></tr>
    <tr><td>8</td><td>2020-11-27</td><td>Surgery Center of Zachary</td><td>-999999</td></tr>
    <tr><td>9</td><td>2020-11-27</td><td>Hospital San Antonio</td><td>11.8</td></tr>
    <tr><td>10</td><td>2020-11-27</td><td>Centro Medico Correccional de Bayamon</td><td>36.7</td></tr>
  </tbody>
</table>




That's a little bit more manageable.

Let's look at some summary statistics to see what this data looks like.


```kotlin
HTML(filtered.statistics.html)
```


<table id="table_id" class="display" style="width:100%">
<thead><tr>
  <th/><th>collection_week</th><th>hospital_name</th><th>all_adult_hospital_inpatient_bed_occupied_7_day_avg</th></thead></tr>
  <tbody>
    <tr><td>count</td><td></td><td></td><td>84097</td></tr>
    <tr><td>NaN</td><td></td><td></td><td>3272</td></tr>
    <tr><td>mean</td><td></td><td></td><td>-112609.63413</td></tr>
    <tr><td>min</td><td></td><td></td><td>-999999</td></tr>
    <tr><td>median</td><td></td><td></td><td>35.7</td></tr>
    <tr><td>max</td><td></td><td></td><td>3825.9</td></tr>
    <tr><td>variance</td><td></td><td></td><td>100023254319.02496</td></tr>
    <tr><td>stddev</td><td></td><td></td><td>316264.53219</td></tr>
    <tr><td>skewness</td><td></td><td></td><td>-2.44946</td></tr>
    <tr><td>kurtosis</td><td></td><td></td><td>-0.02809</td></tr>
    <tr><td>cv</td><td></td><td></td><td>-2.8085</td></tr>
    <tr><td>∑</td><td></td><td></td><td>-9470132401.1999</td></tr>
  </tbody>
</table>




Hmm, that 'min' value looks a little suspicious. This data uses '-999999' to represent the case when the value isn't known for this row. That's definitely going to skew other statistics as well.

When Kane sees a value of Double.NaN it ignores that value in statistics. So let's replace the special value '-999999' with Double.NaN.


```kotlin
val covid = filtered.mapDoubles { if (it.toInt() == -999999) Double.NaN else it }
HTML(covid.statistics.html)
```


<table id="table_id" class="display" style="width:100%">
<thead><tr>
  <th/><th>collection_week</th><th>hospital_name</th><th>all_adult_hospital_inpatient_bed_occupied_7_day_avg</th></thead></tr>
  <tbody>
    <tr><td>count</td><td></td><td></td><td>74619</td></tr>
    <tr><td>NaN</td><td></td><td></td><td>12750</td></tr>
    <tr><td>mean</td><td></td><td></td><td>105.30992</td></tr>
    <tr><td>min</td><td></td><td></td><td>4</td></tr>
    <tr><td>median</td><td></td><td></td><td>47.6</td></tr>
    <tr><td>max</td><td></td><td></td><td>3825.9</td></tr>
    <tr><td>variance</td><td></td><td></td><td>25040.88875</td></tr>
    <tr><td>stddev</td><td></td><td></td><td>158.24313</td></tr>
    <tr><td>skewness</td><td></td><td></td><td>4.84938</td></tr>
    <tr><td>kurtosis</td><td></td><td></td><td>42.17925</td></tr>
    <tr><td>cv</td><td></td><td></td><td>1.50264</td></tr>
    <tr><td>∑</td><td></td><td></td><td>7858120.8</td></tr>
  </tbody>
</table>




That's better. We can see there were 12,750 missing values, leaving 74,619 rows with values.
Also notices the mean, min, and other statistics have changed reflect the removed -999999 values.

Finally, let's save this to a new .csv file so that we can use it for other demos.


```kotlin
covid.writeCsv("data/covid-slim.csv")
```
