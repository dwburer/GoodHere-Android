# GoodHere
CSCI-5331 Final Project

### Lazy man's version of task management (our to-do list):

We'll keep a rough record of changes and tasks here. If you want to add something, just add another line under one of our names.

#### Danny
- [x] Get voting to work, layout visual framework for UI and outline database models.
- [ ] Get `GoodHere-Backened` deployed to AWS (Elasic Beanstalk)
- [ ] Set up database with Amazon RDS - may be handled automatically with EB, probably want to configure ourselves anyway
- [ ] Polish up `EstablishmentDetailView`, including voting interface.
- [ ] Allow users to vote only once. This will require a new `Vote` model to be implemented.
- [ ] Create UserProfile model, add to endpoint and serializers.
- [ ] Integrate Google Maps id's into the Establishment model - this depends on Kelvin's progress on maps stuff.
- [ ] Add search functionality to the List tab - this will probably use DRF's `filter` options.

#### Kelvin
- [ ] Get the ball rolling on Google Maps integration, etc.
- [ ] Design the profile tab. This will eventally pull information from the UserProfile endpoint, but until Danny implements it use example values.
- [ ] Get a splash screen working. This will display for a few seconds while things load, we check for authentication, etc.
- [ ] General code cleanup - just skim the classes every now and then and make sure there are no glaring issues.

We can move these around or trade off if we get stuck and any point.
