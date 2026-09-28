AdminConfig.create('URL', AdminConfig.getid('/Cell:Default01Cell/Node:AppSrv01Node1/Server:server1/URLProvider:Default URL Provider/'), '[[spec "http://api.wunderground.com/api"] [name "WeatherAPI"] [description "Weather API used by modresorts application"] [category "modresorts"] [jndiName "jndi/WeatherAPI"]]') 
AdminConfig.save()
# Sync nodes
AdminControl.invoke('WebSphere:name=DeploymentManager,process=dmgr,platform=common,node=Dmgr01Node,diagnosticProvider=true,version=9.0.5.25,type=DeploymentManager,mbeanIdentifier=DeploymentManager,cell=Default01Cell,spec=1.0', 'multiSync', '[false]', '[java.lang.Boolean]')
