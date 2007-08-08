                           ThinWire(R) Mail Demo
               Copyright (C) 2006-2007 Custom Credit Systems

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option) any
 later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along
 with this library; if not, write to the Free Software Foundation, Inc., 59
 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Users interested in finding out more about the ThinWire framework should visit
 the ThinWire framework website at http://www.thinwire.com. For those interested
 in discussing the details of how this demo was built, you can contact the 
 developer via email at "Ted C. Howard" <tedchoward at gmail dot com>.

===============================================================================
				        About ThinWire(R) Mail Demo	
===============================================================================
The Mail Client Demo is a perfect example of the simplicity of the ThinWire(R)
framework. With minimal lines of code, we have an application that does the
following:

    * connects to a specified POP3 server,
    * downloads all the messages in the INBOX,
    * displays them in a sortable grid,
    * previews plain text and html formatted messages,
    * allows you to quickly search the message grid,
    * and displays a resizeable dialog for creating a new message.

===============================================================================
                           Building the Mail Demo
===============================================================================
The build process for the demo is defined using the Apache Ant build tool. It
has only been built using Ant 1.6 or greater, but it may build correctly with
earlier releases as well.  You can learn about the Apache Ant project and
download a working version from: http://ant.apache.org/

Once you have Ant installed and added to your system path, you can build the
demo simply by typing 'ant dist' at the command shell from the 'build'
directory.  The following Ant build targets are supported:

 dist        compile the demo, create a jar and package
	         it along with other required runtime files into
             a distribution zip.

 dist14      compile a Java 1.4 compatible version of the
             demo, create a jar and package it along with
             other required runtime files into a distribution
             zip.
	
 source      create a source only distribution that contains
             everything necessary to build the demo.
