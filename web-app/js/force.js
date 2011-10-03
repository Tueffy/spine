var w = 1260,
    h = 500,
    fill = d3.scale.category10();

var vis = d3.select("#chart")
  .append("svg:svg")
    .attr("width", w)
    .attr("height", h)
    .append("svg:g")
    .attr("id","spineCanvas");

var link;
var node;
var force;

//var serverName = "http://ec2-46-137-44-25.eu-west-1.compute.amazonaws.com:80";
var serverName = "http://localhost:8080";

function renderCanvas(d){
	
	if (d != userID) {
		//alert(d);
		userID = d.name;
		node.remove();
		link.remove();
	}
	
d3.json(serverName+"/spine/network/graphJSON?filter="+filterString+"&userID="+userID, function(json) {
	force = d3.layout.force()
      .charge(-350)
      .linkDistance(120)
      .nodes(json.nodes)
      .links(json.links)
      .size([w, h])
      .start();

  link = vis.selectAll("line.link")
      .data(json.links)
    .enter().append("svg:line")
      .attr("class", "link")
      .style("stroke-width", function(d) { return Math.sqrt(d.value+2); })
      //.style("stroke-width", 5)
      .attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  node = vis.selectAll("circle.node")
      .data(json.nodes)
      .enter().append("svg:g")
      .attr("class", "node")
      .call(force.drag);
      
      node.append("svg:circle")
      .attr("r", 5)
      .style("fill", function(d) { return fill(d.group); });

  node.append("svg:text")
   .attr("text-anchor", "middle")
   .attr("font-size", "11")
   .text(function(d) { return d.name; });

	link.on("click", showTagsOnLink);
		  
		//alert(d.source.name + " -> "+d.target.name);

	node.on("click", renderCanvas);

  vis.style("opacity", 1e-6)
    .transition()
      .duration(1000)
      .style("opacity", 1);

  force.on("tick", function() {
    link.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });
    
    node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; }); 
  });//close force.on
  
});//close d3.js

} // close renderCanvas

// Init the Canvas (Events are registered inside renderCanvas 
  renderCanvas(userID);


		  
function showTagsOnLink(d) {
// Shows all tags on link d	
	
	d3.json("http://localhost:8080/spine/network/graphEdgesJSON?source="+d.source.name+"&target="+d.target.name, function(json) { 
	alert(json);
		
	}); // end d2.json
	
	} // end showTagsOnLink
  

