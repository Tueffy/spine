<html>
<head>
  	<meta name="layout" content="main">
	<title>Spine Landing</title>
	<link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
</head>
<body>
  	<div class="grid_21 landing" id="left">
      <h2>How it works</h2> 
    </div>
  	<br/>
    <!-- START : LEFT MENU -->
    <div id="left" class="grid_5">     
       <ul class="menu">
        <li><a href="#network">Build your Network</a></li>
        <li><a href="#search">Search for an Expert</a></li>
        <li><a href="#badges">Badges explained</a></li>
      </ul>   
    </div>
    <!-- END : LEFT MENU -->
     <!-- BEGIN : RIGHT COLUMN -->
    <div id="right" class="grid_19 feed">  
    	<div class="grid_19 feed"> 
	    	<div class="grid_6 content">  
	    		<a name="network">Build your Network</a>
	    	</div>
	    	<div class="grid_12 content">  
		    	Building your network is the main important activity within Spine. You create relationships to other people within 
		    	your organisation and with this you build the overall expert network. The other people in your organisation can see 
		    	and benefit from your personal network. They see, whom you consider an expert in certain topics, and can use this 
		    	valuable information when searching for experts by themselves.<br><br>
		    	
		    	The network is displayed in the middle part of the page. In the end you see a list of persons together with tags
		    	and the distance to this person. A distance of 1 means that you have direct tags related to this user. These tags
		    	are displayed in a blue color, for example Cycling and Soccer in the below example. Tags, which have been set
		    	by other persons to that person, are in a grey color.<br><br>
		    	
		    	<img src="${resource(dir:'images/info',file:'spine_network1.png')}" alt="Connected Person"><br><br>
		    	
		    	Below an example for a person not directly connected to the logged in user. A distance of two means that one of 
		    	your direct contacts has tagged this person.<<br><br>

		    	<img src="${resource(dir:'images/info',file:'spine_network2.png')}" alt="Distant Person"><br><br>

		    	Now you could add this person to your network, in case you have experienced that person as an expert in a
		    	certain topic. You can do this directly in this middle part of the page.<br><br>
		    	
		    	One way is to add a tag in the text field and press the + symbol next to it.<br><br>

		    	<img src="${resource(dir:'images/info',file:'spine_network3.png')}" alt="Add new tag"><br><br>

		    	The result can be seen, when re-loading the page. A new tag in blue color is added, your tag. In addition
		    	the distance is now one, because this person is now directly tagged by you.<br><br>

				Furthermore it is possible to use tags, which have been given to that person by another user. In this case
				you can click on the + symbol on an existing tag, in our example Australia.<br><br>
				
		    	<img src="${resource(dir:'images/info',file:'spine_network4.png')}" alt="Reuse a tag"><br><br>

		    	The final result of this activity is displayed below. Two tags are set by you, because this person is
		    	considered by you to be an expert in these two topics.<br><br>

		    	<img src="${resource(dir:'images/info',file:'spine_network5.png')}" alt="Final result"><br><br>

		    	So now go ahead and create your network in order to build the ppine of your organisation!<br><br>
	    	</div>
	    </div>
	    <div class="grid_19 alpha omega close">
        	&nbsp;
   		</div> 
    	<div class="grid_19 feed"> 
	    	<div class="grid_6 content">  
	    		<a name="search">Search for an Expert</a>
	    	</div>
	    	<div class="grid_12 content">  
		    	Spine offers you multiple options to search and find your expert. The most obvious one is to use the
		    	search field, which is placed right in the middle on top of your network. As soon as you start typing 
		    	in the search field, an auto completion mechanism will help you to use the right tags.<br><br>
		    	
		    	<img src="${resource(dir:'images/info',file:'spine_search1.png')}" alt="Search Auto-Completion"><br><br>

				But you can also search for person attributes or names if needed. Multiple search parameters can be concatenated
				either using AND or OR.<br><br>

		    	<img src="${resource(dir:'images/info',file:'spine_search4.png')}" alt="Multi-Search"><br><br>

				Another option is to click on tags. You can click on any tag, either in the list of hot tags...<br><br>

		    	<img src="${resource(dir:'images/info',file:'spine_search2.png')}" alt="Connected Person"><br><br>

		    	...or in the person details.<br><br>

		    	<img src="${resource(dir:'images/info',file:'spine_search3.png')}" alt="Connected Person"><br><br>

		    	So Spine offers you all the possibilities to find your expert for your needs!<br><br>
	    	</div>
	    </div>
	    <div class="grid_19 alpha omega close">
        	&nbsp;
   		</div>   
    	<div class="grid_19 feed"> 
	    	<div class="grid_6 content">  
	    		<a name="badges">Badges explained</a>
	    	</div>
	    	<div class="grid_12 content">  
		    	
				Badges play an important role in Spine. It is used to visually recognize an expert. Badges are defined on
				organisational level. Typically it means that a certain amount of people within the organisation have tagged
				a person with the same tag.<br><br>
				
		    	<img src="${resource(dir:'images/info',file:'spine_badges1.png')}" alt="Person Details"><br><br>

	    	</div>
	    </div>
	    <div class="grid_19 alpha omega close">
        	&nbsp;
   		</div>   
    </div>    
    <!-- END : RIGHT COLUMN -->      
</body>
</html>