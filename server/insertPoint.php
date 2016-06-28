<?php
    include("query.php");
    
    $posx=$_GET['posx'];
    $posy=$_GET['posy'];
    $name=$_GET['name'];
    $r=$_GET['r'];
    $type=$_GET['type'];
    $description=$_GET['description'];
    $day=$_GET['day'];
    $tstart=$_GET['tstart'];
    $tstop=$_GET['tstop'];
    $pattern_move=$_GET['pattern_move'];
    $res=insertData($posx,$posy,$r,$type,$name,$description,$day,$tstart,$tstop,$pattern_move);
    
    echo "fatto";
?>
