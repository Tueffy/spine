<div id="hot_tags" class="container_24">    
  <ul>
    <li><img src="/spine/images/home/hot_tags.png" width="75" height="23" alt="Hot Tags" ></li>
    <g:each in="${hotTags}" var="t" >    	
     <li class="hot_tags" id="hot_tags_soap"><a href="#">#${t}</a></li>
 </g:each>
  </ul>
  <script>var mydrag = new Draggable('hot_tags_soap', { revert: true });</script>
</div>