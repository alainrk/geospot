<html>
    <head>
        <style>
            td{
                width:250px;
            }
        </style>
    </head>
<?php
    include("query.php");
    function getName($uid){
        if($uid=="USER_UNKONW")
            return "(giulio)";
        if($uid=="-1949170730")
            return "(Alain)";
        if($uid=="1974671864")
            return "(Danger)";
        return "";
    }
    //1974671864 giulio
    
    $list=selectLog();
    echo "<table>";
    echo "<tr><td>uid</td><td>time</td><td>latitude</td><td>longitude</td><td>move type</td></tr>";
    foreach($list as $l){
        echo "<tr><td>".$l['uid']." ".getName($l['uid'])."</td><td>".$l['time']."</td><td>".$l['posx']."</td><td>".$l['posy']."</td><td>".getMoveFromId($l['move_type'])."</td></tr>";
    } 
    echo "</table>";
?>

</html>
