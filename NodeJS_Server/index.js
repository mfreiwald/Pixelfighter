var admin = require("firebase-admin");

var serviceAccount = require("./pixelfighter-ws1718-firebase-adminsdk-stz5i-fccd983fd0.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://pixelfighter-ws1718.firebaseio.com"
});

var db = admin.database();
db.ref('/games').on("child_added", function(snapshot) {
  
  var key = snapshot.key;
  var game = snapshot.val();

  if(game.active) {
  	db.ref('games/'+key).on("value", function(snapshot) {
  		var game = snapshot.val();

  		var foundEmpty = false;
		for(var x=0; x<game.board.width; x++) {
			for(var y=0; y<game.board.height; y++) {
				var pixel = game.board.pixels[x][y];
				if(pixel.playerKey == "" && pixel.team == "None") {
					foundEmpty = true;
					break;
				}
			}
			if(foundEmpty) {
				break;
			}
		}

		if(!foundEmpty) {
			console.log("set active on game " + snapshot.key + " to false");
			db.ref('games/'+snapshot.key).update({
				"active": false
			});
		}

  	});
  }

});





/*
db.ref('/games/{gameId}')
	.onWrite(event => {
		var game = event.data.val();
		console.log('Game', event.params.gameId, game);

	  	// check if board is full
		var foundEmpty = false;
		for(var x=0; x<game.board.width; x++) {
			for(var y=0; y<game.board.height; y++) {
				var pixel = game.board.pixels[x][y];
				if(pixel.playerKey == "" && pixel.team == "None") {
					foundEmpty = true;
					break;
				}
			}
			if(foundEmpty) {
				break;
			}
		}

		console.log("Found Empty Pixel: " + foundEmpty);

		if(!foundEmpty) {
			console.log("set active to false");
			return event.data.ref.child("active").set(false);
		}

	   // check if time is over
 		return event.data.val();
});
*/