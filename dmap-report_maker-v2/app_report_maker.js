var cluster = require('cluster');
var express = require('express');
var morgan = require('morgan');
var moment = require('moment');

var logger = require('./lib/logger');

var DONE = 'done';

var rejected = 0;

if (cluster.isMaster) {
	var prossCnt = 0;

	var maker = cluster.fork();
	// var makerBusy = false;

	prossCnt++;
	maker.on('message', function(msg) {
		logger.debug(msg);
	});

	cluster.on('exit', function(worker, code, signal) {
		prossCnt--;
		logger.error('[app/master] worker ' + worker.process.pid
				+ ' died - code : ' + code + ', signal : ' + signal);
	});

	var app = express();
	app.set('port', 9300);
	app.use(morgan('combined', {
		'stream' : logger.stream
	}));
	app.use('/health', function(req, res) {
		var data = {};

		if (prossCnt === 1) {
			data.Result = 'OK';
			data.isBusy = makerBusy;
		} else {
			data.Result = 'ERROR';
			data.isBusy = makerBusy;
			data.Process = prossCnt;
		}

		res.send(data);
	});

	app.get('/report',
		function(req, res, next) {
			// 현재시간
			var date = new Date();
	
			var params = {
				seq : req.query.seq,
				regDt : req.query.regDt,
				typeCd : req.query.typeCd,
				channels : req.query.channels,
				startDate : req.query.startDate,
				endDate : req.query.endDate,
				datasets : req.query.datasets,
				projectSeq : req.query.projectSeq,
				compareYn : req.query.compareYn
			};
	
			maker.send(params);
			maker.on('message', function(msg){
				if(msg == 'S'){
					next(null);
				}else{ // 오류 발생
					next(msg);
				}
			});
		},
		function(req, res, next){
			res.send('OK')
		},
		function(err, req, res, next){
			logger.error(err);
			res.status(500).send("Something broke!");
		}
	);
	
	// error handler
	//app.use();

	var server = app.listen(app.get('port'), function() {
		var host = server.address().address;
		var port = server.address().port;

		logger.info('[app][master] Server Listening on port %d', port);
	});
	
} else {
	//var sleep = require('sleep');
	var exec = require('child_process').exec;
	var elasticsearch = require('elasticsearch');
	
	var es_client_host = 'ec2-13-125-22-244.ap-northeast-2.compute.amazonaws.com:9200';	// PRODUCTION
	var esClient = new elasticsearch.Client({
		host : es_client_host,
		keepAlive: false,
/*		requestTimeout: 60000,
		timeout : 60000,
		keepAliveMsecs: 1000,
		maxSockets: "Infinity",
		maxFreeSockets: 256,
		freeSocketKeepAliveTimeout: 120000,
		keepAliveTimeout: 150000,*/
		log: 'info'
	});
	
	var getNumberOfRejectedTasks = function(callback){
		esClient.cat.threadPool(
			{
				threadPoolPatterns:'search',
				h:'queue,queue_size,active,rejected,completed,keep_alive,min,max,node_name',
				format:'json'
			},
			function(err, result){
				if (err){
					logger.error(err);
					callback(err);
				}else{
					callback(null, result);
				}
			}
		);
	}
	
	process.on('message', function(msg) {
		getNumberOfRejectedTasks(function(err, result){
			console.log(err);
			
	        for (var idx=0; idx<result.length; idx++){
	        	console.log(result);
	        	
	        	// rejected 개수가 늘었다면 현재 ES에서 Rejected Exception을 발생시키는 것으로 간주하고 에러 리턴
	        	if(result[idx].rejected > rejected){
	        		rejected = result[idx].rejected; 
	                
	        		logger.error("[app][maker] EsRejectedExecutionException ")
	        		logger.error(err);
					process.send(err);
	                
	        	} else { // rejected 개수가 늘지 않았다면
	        		logger.info('[app][maker] Report 생성 요청');
	        		var child = exec(__dirname + '/pythonwrapper.sh'+ ' ' + msg.seq
																	+ ' ' + msg.regDt
																	+ ' ' + msg.typeCd
																	+ ' ' + msg.channels
																	+ ' ' + msg.startDate
																	+ ' ' + msg.endDate
																	+ ' ' + msg.datasets
																	+ ' ' + msg.projectSeq
																	+ ' ' + msg.compareYn,
									{ maxBuffer : 1024 * 1024 * 200 }, 
	        						function( err, stdout, stderr) {
	        							if (err) {
	        								logger.error("[app][maker] Executing pythonwrapper.sh has errors.");
	        								logger.error(err);
	        								process.send(err);
	        							} else {
	        								logger.info(stdout);
	        								process.send("S");
	        							}
	        						}); // exec
	        	}
	        }
		});
	});
	
	
}


