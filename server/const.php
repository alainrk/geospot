<?php
    /*Each moves has a specific area of relevance
     * this structure relate move code (3,7,2) with his name
     * and his area in meters.
     * Still has 3 as key code and 50 meters as radius
     **/
    $MOVES=[
        3 => ["still",50],//50
        7 => ["walking",100],//100
        2 => ["walking",100],//100 foot
        8 => ["running",100],//100
        0 => ["veicle",500],//500
        1 => ["biciycle",250]//250
    ];
    
    function getIdFromMove($move){
        return -1;
    }
    
    function getMoveFromId($id){
		 global $MOVES;
        return $MOVES[$id][0];
    }
    
    function getAreaFromId($id){
        global $MOVES;
        //echo $id;
        //var_dump($MOVES[$id]);
        return $MOVES[$id][1];
    }

?>
