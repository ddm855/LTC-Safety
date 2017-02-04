//
//  LTCConcernViewModel.m
//  LTC Safety
//
//  Created by Allan Kerr on 2017-01-27.
//  Copyright © 2017 CS371 Group 2. All rights reserved.
//

#import "LTCConcernViewModel.h"

@interface LTCConcernViewModel () <NSFetchedResultsControllerDelegate>
@property (strong, nonatomic) NSFetchedResultsController *fetchedResultsController;
@property (readwrite, strong, nonatomic) NSManagedObjectContext *objectContext;
@end

@implementation LTCConcernViewModel
@dynamic sectionCount;

- (instancetype)initWithContext:(NSManagedObjectContext *)context {
    
    NSAssert(context != nil, @"Attempted to initialize the concern view model with a nil context.");
    
    if (self = [super init]) {
        self.fetchedResultsController = [self _loadFetchedResultsControllerForContext:context];
        
        NSAssert(self.fetchedResultsController != nil, @"Failed to load fetched results controller.");

        self.fetchedResultsController.delegate = self;
        self.objectContext = context;
    }
    return self;
}

#pragma mark - Fetched results controller

- (NSFetchedResultsController *)_loadFetchedResultsControllerForContext:(NSManagedObjectContext *)context {
    
    NSFetchRequest *fetchRequest = [LTCConcern fetchRequest];
    [fetchRequest setFetchBatchSize:20];
    
    // Sort based on submission date
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"submissionDate" ascending:NO];
    fetchRequest.sortDescriptors = @[sortDescriptor];
    
    NSFetchedResultsController *fetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:context sectionNameKeyPath:nil cacheName:@"Master"];
    fetchedResultsController.delegate = self;
    
    NSError *error = nil;
    if (![fetchedResultsController performFetch:&error]) {
        // TODO properly handle error
        NSLog(@"Unresolved error %@, %@", error, error.userInfo);
        abort();
    }
    return fetchedResultsController;
}

- (NSUInteger)sectionCount {
    
    NSAssert(self.fetchedResultsController.sections.count > 0, @"Invalid number of sections for fetched results controller.");
    
    return self.fetchedResultsController.sections.count;
}

- (NSUInteger )rowCountForSection:(NSUInteger)section {
    
    NSAssert1(section >= 0 && section < self.fetchedResultsController.sections.count, @"Section out of bounds for row count: %lu", section);
    
    id <NSFetchedResultsSectionInfo> sectionInfo = [self.fetchedResultsController sections][section];
    return [sectionInfo numberOfObjects];
}

- (void)addConcern:(LTCConcern *)concern error:(NSError **)error {
    
    NSAssert(concern != nil, @"Attempted to add a nil concern.");
    NSAssert(error != nil, @"Attempted to call add concern without an error handler.");
    
    [self.objectContext save:error];
}

- (LTCConcern *)concernAtIndexPath:(NSIndexPath *)indexPath {
    
    NSAssert1(indexPath.section >= 0 && indexPath.section < self.fetchedResultsController.sections.count, @"Attempted to get concern in out of bounds section: %lu", indexPath.section);
    
    return [self.fetchedResultsController objectAtIndexPath:indexPath];
}

- (void)removeConcern:(LTCConcern *)concern error:(NSError **)error {
    
    NSAssert(concern != nil, @"Attempted to remove a nil concern.");
    NSAssert(error != nil, @"Attempted to call remove concern without an error handler.");

    NSManagedObjectContext *context = [self.fetchedResultsController managedObjectContext];
    [context deleteObject:concern];
    [self.objectContext save:error];
}

#pragma mark - NSFetchedResultsController delegate

- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    
    NSAssert(self.delegate != nil, @"Attempted to begin updates on nil delegate.");

    [self.delegate viewModelWillBeginUpdates:self];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    
    NSAssert(self.delegate != nil, @"Attempted to finish updates on nil delegate.");

    [self.delegate viewModelDidFinishUpdates:self];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject
       atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type
      newIndexPath:(NSIndexPath *)newIndexPath {

    NSAssert(self.delegate != nil, @"Attempted to notify a nil delegate of a changed object.");
    
    switch(type) {
        case NSFetchedResultsChangeInsert:
            [self.delegate viewModel:self didInsertConcernsAtIndexPaths:@[newIndexPath]];
            break;
            
        case NSFetchedResultsChangeDelete:
            [self.delegate viewModel:self didDeleteConcernsAtIndexPaths:@[indexPath]];
            break;
            
        case NSFetchedResultsChangeUpdate:
            NSAssert1([anObject isKindOfClass:[LTCConcern class]], @"Unexpected object did change in results controller: %@", anObject);
            [self.delegate viewModel:self didUpdateConcern:anObject atIndexPath:indexPath];
            break;
            
        case NSFetchedResultsChangeMove:
            [self.delegate viewModel:self didMoveConcernFromIndexPath:indexPath toIndexPath:newIndexPath];
            break;
    }
}

@end