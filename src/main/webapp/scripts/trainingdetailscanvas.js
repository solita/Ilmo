var TrainingDetailsCanvas = (function() {

  // const ei toimi ie:lla
  var backWidth = 28;
  var backHeight = 18;
  var seatWidth = 20;
  var seatHeight = 15;
  var spaceForSeat = backWidth + 8;
  var backDiff = (backWidth - seatWidth) >> 1;
  var topY = 70;
  var topNameY = topY - 5; 
  var bottomY = 200;
  var bottomNameY = bottomY+seatHeight+backHeight+10; 
  var screenDiff = 15;
  var screenHeight = bottomY - topY + backHeight + seatHeight;  
  var screenWidth = 40;
  var spaceBetweenScreenAndTable = 24;

  function calculateChairsWidth(details) {
      var seats = (details.maxparticipants >> 1);
      if ( details.maxparticipants % 2 == 1 ) seats += 1;
      
      return spaceForSeat * seats - (spaceForSeat - backWidth);
  }

  function drawTable(ctx, width) {
    ctx.roundRect(0, topY+backHeight+seatHeight+10, width, bottomY-10, 20);
    ctx.stroke();
    ctx.fillStyle = "orange";
    ctx.fill();
  }

  function drawSeat(ctx, x, y, fillStyle) {
    ctx.fillStyle = "black";
    ctx.strokeRect(x, y, seatWidth, seatHeight);
    ctx.fillStyle = fillStyle;  
    ctx.fillRect(x, y, seatWidth, seatHeight); 
  }
  
  function drawScreen(ctx, x, y, details) {
    ctx.beginPath();  
    ctx.moveTo(x, y + screenDiff);  
    ctx.lineTo(x+screenWidth, y);  
    ctx.lineTo(x+screenWidth, y+screenHeight);  
    ctx.lineTo(x, y+screenHeight- screenDiff);  
    ctx.closePath();
    ctx.fillStyle = "black";
    ctx.stroke();
    ctx.fillStyle = "#DDDDDD";  
    ctx.fill();
    
    drawText(ctx, x + screenWidth - 12, y + (screenWidth >> 1), Math.PI/2, limitTo(details.name, 26));
    
    var organizer = limitTo("by " + details.organizer, 26);
    drawText(ctx, x + screenWidth - 28, y + (screenWidth >> 1) + 10, Math.PI/2, organizer);
  }
  
  function limitTo(str, charCount) { 
    if ( str.length > charCount ) return str.substr(0,24) + "..";
    return str;
  }
  
  function drawBackRest(ctx, x, y, fillStyle) {
    ctx.beginPath();  
    ctx.moveTo(x, y);  
    ctx.lineTo(x+backWidth, y);  
    ctx.lineTo(x+backWidth-backDiff, y+backHeight);  
    ctx.lineTo(x+backDiff, y+backHeight);  
    ctx.closePath();
    ctx.fillStyle = "black";
    ctx.stroke();
    ctx.fillStyle = fillStyle;  
    ctx.fill();
  }
  
  function drawBackRestMirrored(ctx, x, y, fillStyle) {
    ctx.beginPath();  
    ctx.moveTo(x+backDiff, y);  
    ctx.lineTo(x+backWidth-backDiff, y);  
    ctx.lineTo(x+backWidth, y+backHeight);  
    ctx.lineTo(x, y+backHeight);  
    ctx.closePath();
    ctx.fillStyle = "black";
    ctx.stroke();
    ctx.fillStyle = fillStyle;  
    ctx.fill();
  }
  
  function drawChair(ctx, x, y, fillStyle) {      
    ctx.globalAlpha = 1.0;
    drawBackRest(ctx, x, y, fillStyle);  
    drawSeat(ctx, x+backDiff, y+backHeight+1, fillStyle); 
  }

  function drawSeats(context, details) {
    var angle1 = -Math.PI / 6;
    var angle2 = Math.PI / 6;
  
    var topseatCount = 0;
    var bottomseatCount = 0;
  
    for (var i = 0; i < details.maxparticipants; i++) {
      if ( i < details.participants.length ) {
        if ( i % 2 == 0 ) {
          var x = topseatCount*spaceForSeat;
          ++topseatCount;
          drawChair(context, x, topY, "red");
          drawText(context, x+(backWidth>>1), topNameY, angle1, details.participants[i]);
        }
        else {
          var x = bottomseatCount*spaceForSeat;
          ++bottomseatCount;
          drawChairMirrored(context, x, bottomY, "red");
          drawText(context, x+(backWidth>>1), bottomNameY, angle2, details.participants[i]);
        }
      }
      else {
        if ( i % 2 == 0 ) {
          var x = topseatCount*spaceForSeat;
          ++topseatCount;
          drawChair(context, x, topY, "green");
        }
        else {
          var x = bottomseatCount*spaceForSeat;
          ++bottomseatCount;
          drawChairMirrored(context, x, bottomY, "green");
        }
      }
    }    
  }

  function drawChairMirrored(ctx, x, y, fillStyle) {      
    ctx.globalAlpha = 1.0;
    drawBackRestMirrored(ctx, x, y+seatHeight, fillStyle);  
    drawSeat(ctx, x+backDiff, y, fillStyle); 
  }
 
  CanvasRenderingContext2D.prototype.roundRect = function(sx,sy,ex,ey,r) {
    var r2d = Math.PI/180;
    if( ( ex - sx ) - ( 2 * r ) < 0 ) { r = ( ( ex - sx ) / 2 ); } //ensure that the radius isn't too large for x
    if( ( ey - sy ) - ( 2 * r ) < 0 ) { r = ( ( ey - sy ) / 2 ); } //ensure that the radius isn't too large for y
    this.beginPath();
    this.moveTo(sx+r,sy);
    this.lineTo(ex-r,sy);
    this.arc(ex-r,sy+r,r,r2d*270,r2d*360,false);
    this.lineTo(ex,ey-r);
    this.arc(ex-r,ey-r,r,r2d*0,r2d*90,false);
    this.lineTo(sx+r,ey);
    this.arc(sx+r,ey-r,r,r2d*90,r2d*180,false);
    this.lineTo(sx,sy+r);
    this.arc(sx+r,sy+r,r,r2d*180,r2d*270,false);
    this.closePath();
  }
  
  function drawText(context, x, y, angle, texti, align) {
    if ( align == null ) align = "left";
    context.save();
    context.translate(x, y);
    context.rotate(angle);
    context.textAlign = align;
    context.fillStyle = "black";
    context.fillText(texti, 0, 0);
    context.restore();
  }

  return {
    height: function() { return 300; },
    
    width: function(details) { return calculateChairsWidth(details) + spaceBetweenScreenAndTable + screenWidth; },
    
    clear: function(context) { context.clearRect(0, 0, context.canvas.width, context.canvas.height) },
    
    draw: function(context, details) {
      var chairsWidth = calculateChairsWidth(details);
      
      drawTable(context, chairsWidth);
      drawScreen(context, chairsWidth+spaceBetweenScreenAndTable, topY, details);
      drawSeats(context, details);
	}
  }

 })();
