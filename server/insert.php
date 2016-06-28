<html>
    <head>
        <title>Insert point</title>
        <style>
            label{
                width:200px;
                display:block;
            }
            input{
                width:200px;
            }
            select{
                width:200px;
            }
        </style>
    </head>
    <body>
        <form action="insertPoint.php" method="gets">
                <label>Posx</label><input id="posx" name="posx" type="text" /><br />
                <label>Posy</label><input id="posy" name="posy" type="text" /><br />
                <label>r</label><input id="r" name="r" type="text" value="200" /><br />
                <label>type</label><input id="type" name="type" type="text" value="POI" /><br />
                <label>name</label><input id="name" name="name" type="text" /><br />
                <label>description</label><input id="description" name="description" type="text" /><br />
                <label>day</label><input id="day" name="day" type="text" value="8" /><br />
                <label>tstart</label><input id="tstart" name="tstart" type="text" value="0" /><br />
                <label>tstop</label><input id="tstop" name="tstop" type="text" value="24" /><br />
                <label>Patern Move</label><select id="pattern_move" name="pattern_move" type="text" />
                <option value="-1">all</option>
                <option value="3">still</option>
                <option value="7">walking</option>
                <option value="8">running</option>
                <option value="1">bicycle</option>
                <option value="0">veicle</option>
                
                </select ><br /><br />
                <input type="submit" value="Submit">
        </form>
    </body>
</html>
