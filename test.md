```kotlin
val html = """
<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.23/css/jquery.dataTables.css\">
  
<script type=\"text/javascript\" charset=\"utf8\" src=\"https://cdn.datatables.net/1.10.23/js/jquery.dataTables.js\">
$(document).ready( function () {
    $('#table_id').DataTable();
} );
</script>

<table id="table_id" class="display">
    <thead>
        <tr>
            <th>Column 1</th>
            <th>Column 2</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Row 1 Data 1</td>
            <td>Row 1 Data 2</td>
        </tr>
        <tr>
            <td>Row 2 Data 1</td>
            <td>Row 2 Data 2</td>
        </tr>
    </tbody>
</table>
"""
HTML(html)
```





<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.23/css/jquery.dataTables.css\">

<script type=\"text/javascript\" charset=\"utf8\" src=\"https://cdn.datatables.net/1.10.23/js/jquery.dataTables.js\">
$(document).ready( function () {
    $('#table_id').DataTable();
} );
</script>

<table id="table_id" class="display">
    <thead>
        <tr>
            <th>Column 1</th>
            <th>Column 2</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Row 1 Data 1</td>
            <td>Row 1 Data 2</td>
        </tr>
        <tr>
            <td>Row 2 Data 1</td>
            <td>Row 2 Data 2</td>
        </tr>
    </tbody>
</table>





```kotlin

```