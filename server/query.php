<?php
	include 'medoo.php';
    include 'const.php';
	function connect(){
		$database = new medoo([
			'database_type' => 'sqlite',
			'database_file' => 'gfdag.sqlite' 
		]);
		return $database;
	}
    /*
     * $x the float coords of the latitudes
     * $y the float coords of the longitudes
     * $dir the degrees from north
     * $pattern_move the id of associated kind of move (car, foot, running)
     * $speed the speed of the movment
     * return the json of the requested data
     */
    function getDataset($x,$y,$dir,$pattern_move,$speed){
        $database = connect();
        $day_of_week=date('w');
        $hour_of_day=date('H');    
        
        $pattern_radius=getAreaFromId($pattern_move);
        $xmax = $x + (180/pi())*($pattern_radius/6378137);
        $ymax = $y + (180/pi())*($pattern_radius/6378137)/cos($x);
        $xmin = $x - (180/pi())*($pattern_radius/6378137);
        $ymin = $y - (180/pi())*($pattern_radius/6378137)/cos($x);

        $dati=$database->select('data', [
                         'id',
                         'name',
                         'x',
                         'y',
                         'type',
                         'r',
                         'description'],
                 [
                    'AND'=>[
                        'move_type[=]'=>[-1,$pattern_move],
                        'x[>]'=> $xmin,
                        'x[<]'=> $xmax,
                        'y[>]'=> $ymin,
                        'y[<]'=> $ymax,
                        'day[=]'=>[8,$day_of_week],
                        'tstart[<]'=>$hour_of_day,
                        'tstop[>]'=>$hour_of_day
                    ]
                 ]);
        return $dati;
    }
    
    /*Insert new point in the database */
    function insertData($x,$y,$r,$type,$name,$description,$day,$tstart,$tstop,$move_type){
        $database = connect();
        $res=$database->insert('data', [
                         'name'=>$name,
                         'x'=>$x,
                         'y'=>$y,
                         'type'=>$type,
                         'r'=>$r,
                         'description'=>$description,
                         'day'=>$day,
                         'tstart'=>$tstart,
                         'tstop'=>$tstop,
                         'move_type'=>$move_type
        ]);
        var_dump($database->error());
        return $res;
    }
    
    /*Insert a log for each request received */
    function insertLog($uid,$x,$y,$move_type){
        $database = connect();
        $res=$database->insert('log', [
                         'uid'=>$uid,
                         'posx'=>$x,
                         'posy'=>$y,
                         'move_type'=>$move_type
        ]);
        return $res;
    }

    /*Select the log list */
    function selectLog(){
        $database = connect();
        $res=$database->select('log', [
                         'uid',
                         'posx',
                         'posy',
                         'move_type',
                         'time'
        ]);
        return $res;
    }
?>
