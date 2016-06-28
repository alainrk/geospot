<?php
    include("query.php");
    //insertData($x,$y,$r,$type,$name,$description,$day,$tstart,$tstop)
    
    $coord="44.496717,11.3541069";
    $c=explode(",",$coord);
    //$res=insertData($c[0],$c[1],"10","POI","Lab Informatico","Apertura straordinaria del laboratorio al pubblico",0,null,null);
    
    
    $poi=["44.496266,11.3497825", //piazza verdi
    "44.498359,11.35459", //ZTL san donato
    "44.505614,11.3412749",//sciopero dei treni
    "44.500502,11.3431749",//piazzola
    "44.518525,11.2772079",//casa giulio
    "44.508775,11.3465559",//black fire
    "44.5761552,11.3619577",//casa danger
    "44.499112,11.3422426",//via indipendenza
    "44.494854, 11.342627",//via indipendenza fine
    "44.495436,11.3369779",//ugo bassi fine
    "44.494196,11.3442739"];//torri 
    
    
    $name=[
        "piazza verdi",
        "ZTL San Donato",
        "Sciopero",
        "Sconti pizza",
        "Cinema ~ cinelli",
        "Black Fire Birra 25%",
        "Sessione D&D",
        "TDays Indipendenza",
        "TDays Indipendenza",
        "TDays Ugo Bassi",
        "TDays Torry"
    ];
    
    for($i=0;$i<count($poi);$i=$i+1){
        $c=explode(",",$poi[$i]);
        $res=insertData($c[0],$c[1],"10","POI",$name[$i],$name[$i]."placeholder description",0,null,null);
        echo $res."<br />";
    }
    
    echo "fatto";
?>
