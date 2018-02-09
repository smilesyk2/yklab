var cluster = require('cluster');
var express = require('express');
var morgan = require('morgan');
var moment = require('moment');

var logger = require('./lib/logger');

var DONE = 'done';

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
	app.set('port', 8080);
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

	app.get('/report', function(req, res) {
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

		var data = {};
		data.Result = 'OK';

		maker.send(params);
		res.send(data);

	});

	var server = app.listen(app.get('port'), function() {
		var host = server.address().address;
		var port = server.address().port;

		logger.info('[app][master] Server Listening on port %d', port);
	});
	
} else {
	//var sleep = require('sleep');
	var exec = require('child_process').exec;

	process.on('message', function(msg) {
		logger.info('[app][maker] Report 생성 요청');
		var child = exec(__dirname + '/pythonwrapper.bat'
									+ ' ' + msg.seq
									+ ' ' + msg.regDt
									+ ' ' + msg.typeCd
									+ ' ' + msg.channels
									+ ' ' + msg.startDate
									+ ' ' + msg.endDate
									+ ' ' + msg.datasets
									+ ' ' + msg.projectSeq
									+ ' ' + msg.compareYn, { maxBuffer : 1024 * 1024 * 200 }, 
						function( err, stdout, stderr) {
							if (err) {
								logger.error(err);
								process.send(err);
							} else {
								logger.info(stdout);
								process.send("S");
								//sleep.sleep(3); 
							}
						}); // exec
	});
	
	
}
