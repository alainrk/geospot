<?php
    include("query.php");
    $uid=$_GET['uid'];
    $x=$_GET['posx'];
    $y=$_GET['posy'];
    $dir=$_GET['dir'];
    $pattern_move=$_GET['pattern_move'];
    $speed=$_GET['speed'];
    $res=getDataset($x,$y,$dir,$pattern_move,$speed);
    insertLog($uid,$x,$y,$pattern_move);
    echo json_encode($res);
?>
