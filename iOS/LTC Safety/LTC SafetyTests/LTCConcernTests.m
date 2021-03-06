//
//  LTCConcernTests.m
//  LTC Safety
//
//  Created by Allan Kerr on 2017-02-01.
//  Copyright © 2017 CS371 Group 2. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "GTLRClientObjects_Testing.h"
#import "LTCConcern+CoreDataClass.h"
#import "LTCCoreDataTestCase.h"

/**
 Unit tests for the LTCConcern Core Data class.
 */
@interface LTCConcernTests : LTCCoreDataTestCase
/**
 The datastore concern that will be used to compare with the local concern.
 */
@property (nonatomic, strong) GTLRClient_Concern *concern;
@end


@implementation LTCConcernTests

- (void)setUp {
    [super setUp];
}

- (void)tearDown {
    [super tearDown];
}

/**
 Tests the concern with data method by creating a local concern and a datastore concern then comparing them to make sure all of their data is the same.
 */
- (void)testConcernWithData {

    // Use the testing class extension to get a test concern
    GTLRClient_Concern *concern = [GTLRClient_Concern testConcern];

    LTCConcern *localConcern = [LTCConcern concernWithData:concern ownerToken:@"token" inManagedObjectContext:self.context];
    XCTAssertNotNil(localConcern);
    
    XCTAssertTrue([concern.submissionDate.date isEqualToDate:localConcern.submissionDate]);
    
    XCTAssertTrue([concern.identifier.stringValue isEqualToString:localConcern.identifier]);
    XCTAssertEqual(concern.data.actionsTaken, localConcern.actionsTaken);
    XCTAssertEqual(concern.data.descriptionProperty, localConcern.descriptionProperty);
    XCTAssertEqual(concern.data.concernNature, localConcern.concernNature);
    
    XCTAssertEqual(concern.data.reporter.phoneNumber, localConcern.reporter.phoneNumber);
    XCTAssertEqual(concern.data.reporter.email, localConcern.reporter.email);
    XCTAssertEqual(concern.data.reporter.name, localConcern.reporter.name);

    XCTAssertEqual(concern.data.location.facilityName, localConcern.location.facilityName);
    XCTAssertEqual(concern.data.location.roomName, localConcern.location.roomName);
    
    for (int i = 0; i < concern.statuses.count; i++) {
        GTLRClient_ConcernStatus *status = [concern.statuses objectAtIndex:i];
        LTCConcernStatus *localStatus = [localConcern.statuses objectAtIndex:i];

        XCTAssertEqual(status.type, localStatus.concernType);
        XCTAssertTrue([status.creationDate.date isEqualToDate:localStatus.creationDate]);
    }
}

@end
