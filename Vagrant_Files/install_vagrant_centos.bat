rem The virtual machine is listening on the port 2222 for SSH. 
Vagrant.configure(2) do |config| 
config.vm.box = "box-cutter/centos72" 
  # Redirect Tomcat Port 
config.vm.network "forwarded_port", guest: 8080, host: 8080 
  # Redirect JPDA Debugging Port 
config.vm.network "forwarded_port", guest: 8000, host: 8000 
  # Redirect Custom NodeJS port 
config.vm.network "forwarded_port", guest: 9000, host: 9000 
config.vm.provider "virtualbox" do |v| 
v.memory = 4096 
v.cpus = 1 
  end 
end 
