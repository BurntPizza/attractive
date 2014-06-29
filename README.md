Attractive
===========

######Renders [Peter de Jong attractors](http://paulbourke.net/fractals/peterdejong/) for your entertainment!  
  
A small project of mine, but I am proud of the results and they are actually interesting so I thought I'd put it here.  
  
>In dynamical systems, an attractor is a set of physical properties toward which a system tends to evolve, regardless of the starting conditions of the system.  
An attractor is called strange if it has a fractal structure. This is often the case when the dynamics on it are chaotic, but strange nonchaotic attractors also exist.  
  [(Wikipedia)](http://en.wikipedia.org/wiki/Attractor#Strange_attractor)  
    
Speficially, the Peter de Jong attractor is an attractor defined by this system:
![Imgur](http://i.imgur.com/KCbLplU.png)  
  
This system is quite chaotic, in that even a slight adjustment of the intial conditions or constants in the system *(a, b, c, d)* causes the system to manifest in a dramically different way. To create animation, the renderer increments the constants by a small delta each frame to update the image, changing the trajectory of the system.
The visual representation of the attractor is a point cloud of many iterations of the system, a trace of the trajectory.  
As the image is digitally sampled and thus aliased, it can be thought of as a two-dimensional probability histogram of the points in the set.  
The renderer also colors the points where the color is a function of the 'velocity' of the system, or the Euclidean distance between successive points, a purely aestetic decision. The intensity is the probablity, so bright pixels are where many points are located.

######Some images:  
These are 600,000 points per frame:

![Imgur](http://i.imgur.com/eQwJ6wW.gif)
![Imgur](http://i.imgur.com/Qw2ujLz.gif)
![Imgur](http://i.imgur.com/mqfg2Dp.gif)  
  
\<More to come\>