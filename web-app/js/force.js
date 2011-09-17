var w = 1260,
    h = 500,
    fill = d3.scale.category10();

var vis = d3.select("#chart")
  .append("svg:svg")
    .attr("width", w)
    .attr("height", h)
    .append("svg:g");

d3.json("http://localhost:8080/spine/network/graphJSON?filter="+filterString+"&userID="+userID, function(json) {
	var force = d3.layout.force()
      .charge(-350)
      .linkDistance(120)
      .nodes(json.nodes)
      .links(json.links)
      .size([w, h])
      .start();

  var link = vis.selectAll("line.link")
      .data(json.links)
    .enter().append("svg:line")
      .attr("class", "link")
      .style("stroke-width", function(d) { return Math.sqrt(d.value+2); })
      //.style("stroke-width", 5)
      .attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  var node = vis.selectAll("circle.node")
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

 

  link.on("click", function(d) {
	  alert(d.source.name + " -> "+d.target.name);

  });


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
  });

  node.on("click", function(d) {
	  var guy = d;
	  d3.json("http://localhost:8080/spine/network/graphJSON?filter=&userID="+d.name, function(json) {
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
	      .attr("x1", function(d) { return guy.x; })
	      .attr("y1", function(d) { return guy.y; })
	      .attr("x2", function(d) { return d.target.x; })
	      .attr("y2", function(d) { return d.target.y; });;
	      
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
	  });

  });  
  
});
