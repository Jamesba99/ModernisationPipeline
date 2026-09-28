# Enterprise Applications > Enterprise Applications
AdminApp.install('/home/itzuser/Student/assets/modresorts-2.0.0.war', '[  -nopreCompileJSPs -distributeApp -nouseMetaDataFromBinary -appname modresorts-2_0_0_war -createMBeansForResources -noreloadEnabled -nodeployws -validateinstall warn -noprocessEmbeddedConfig -filepermission .*\.dll=755#.*\.so=755#.*\.a=755#.*\.sl=755 -noallowDispatchRemoteInclude -noallowServiceRemoteInclude -asyncRequestDispatchType DISABLED -nouseAutoLink -noenableClientModule -clientMode isolated -novalidateSchema -contextroot /resorts  -MapModulesToServers [[ modresorts-2.0.0.war modresorts-2.0.0.war,WEB-INF/web.xml WebSphere:cell=Default01Cell,node=AppSrv01Node1,server=server1 ]]]' )
AdminConfig.save()
# Sync nodes
AdminControl.invoke('WebSphere:name=DeploymentManager,process=dmgr,platform=common,node=Dmgr01Node,diagnosticProvider=true,version=9.0.5.25,type=DeploymentManager,mbeanIdentifier=DeploymentManager,cell=Default01Cell,spec=1.0', 'multiSync', '[false]', '[java.lang.Boolean]')

