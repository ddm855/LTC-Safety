/**
 * Created by allankerr on 2017-02-13.
 *
 * Unit tests for the inbox controller to ensure that concerns are properly fetched
 * from the Admin API and that paging functions behave as expected.
 */
describe("Inbox Controller", function() {

    beforeEach(module('safetyApp'));

    var $scope;
    var adminApiMock;
    var firebaseMock;
    var $controller;

    beforeEach(inject(function(_$controller_){

        $controller = _$controller_;

        $scope = {
            $apply : function() {}
        };

        // AdminApi mock to mock out server concern calls
        adminApiMock = {
            requestConcernList: function(request) {
                return {
                    execute: function(callback) {
                        var response = {
                            concernList : ['test1', 'test2']
                        };
                        callback(response);
                    }
                }
            }
        };

        // Firebase mock to mock out authentication server calls
        firebaseMock = {
            auth: function() {
                return {
                    onAuthStateChanged: function(callback) {}
                };
            }
        };
    }));

    /**
     * Unit tests for the inbox controllers request concerns function
     * for fetching concerns from the backend using the Admin API.
     */
    describe('Inbox concern request tests', function() {

        /**
         * Test to ensure that the list of concerns is updated when update concern list is called.
         */
        it('Request concerns test', function() {

            var controller = $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });

            $scope.concernRequest.accessToken = "fakeAccessToken";
            $scope.updateConcernList();

            // Expect that the concerns list is non-empty
            expect($scope.concerns.concernList).toEqual(['test1', 'test2']);

        });

        /**
         * Test to ensure that the list of concerns is not updated when no access token is provided
         */
        /**
         * Test to ensure that the list of concerns is not updated when no access token is provided
         */
        it('Request concerns without token test', function() {

            var controller = $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });

            $scope.concernRequest.limit = 25;
            $scope.concernRequest.offset = 0;

            // Expect that the concerns list is not updated
            expect($scope.updateConcernList).toThrow(new Error("Attempted to refresh the concerns list with a null access token."));
            expect($scope.concerns.concernList).toEqual([]);
        });

        /**
         * Test to ensure that the list of concerns is not updated when an invalid page offset is provided
         */
        it('Request concerns without negative page offset test', function() {

            var controller = $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });


            $scope.concernRequest.accessToken = "fakeAccessToken";
            $scope.concernRequest.limit = 25;
            $scope.concernRequest.offset = -1;

            // Expect that the concerns list is not updated
            expect($scope.updateConcernList).toThrow(new Error("Attempted to fetch a page with a negative start index."));
            expect($scope.concerns.concernList).toEqual([]);
        });

        /**
         * Test to ensure that the list of concerns is not updated when an invalid page offset is provided
         */
        it('Request concerns with non-positive page limit test', function() {

            var controller = $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });

            $scope.concernRequest.accessToken = "fakeAccessToken";
            $scope.concernRequest.limit = 0;
            $scope.concernRequest.offset = 0;

            // Expect that the concerns list is not updated
            expect($scope.updateConcernList).toThrow(new Error("Attempted to fetch an empty page."));
            expect($scope.concerns.concernList).toEqual([]);
        });
    });

    /**
     * Unit tests for the inbox controllers nextPage, previousPage, and refresh
     * functions for fetching concerns from the backend using the Admin API.
     */
    describe('Inbox paging tests', function() {

            /**
         * Test to check that the list of concerns is repopulated from the AdminApi when refresh is called.
         */
        it('Refresh concerns test', function() {

            var controller = $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });

            $scope.concernRequest.accessToken = "fakeAccessToken";
            $scope.concernRequest.limit = 25;
            $scope.concernRequest.offset = 0;
            $scope.concerns = {};
            $scope.refresh();

            // Expect that the concerns list was repopulated
            expect($scope.concerns.concernList).toEqual(['test1', 'test2']);
        });

        /**
         * Test to check that a URL change occurs with a new offset occurs when nextPage() is called.
         */
        it('Next page test', function() {

            var limit = 25;
            var offset = 0;
            var archived = false;

            // Mock out location to catch the page redirect
            var $location = {
                url: function(path) {
                    expect(path).toEqual('/inbox/' + limit + '/' + limit + '/' + archived)
                }
            };

            var controller = $controller('InboxCtrl', { $scope: $scope, $location: $location, firebase: firebaseMock, adminApi: adminApiMock });

            $scope.concerns.totalItemsCount = 500;
            $scope.concernRequest.accessToken = "fakeAccessToken";
            $scope.concernRequest.archived = archived;
            $scope.concernRequest.offset = offset;
            $scope.concernRequest.limit = limit;

            $scope.nextPage();
        });

        /**
         * Test to check that a URL change occurs with a new offset occurs when previousPage() is called.
         */
        it('Previous page test', function() {

            var limit = 25;
            var offset = 25;
            var archived = false;

            // Mock out location to catch the page redirect
            var $location = {
                url: function(path) {
                    expect(path).toEqual('/inbox/' + (offset - limit) + '/' + limit + '/' + archived)
                }
            };

            var controller = $controller('InboxCtrl', { $scope: $scope, $location: $location, firebase: firebaseMock, adminApi: adminApiMock });

            $scope.concernRequest.accessToken = "fakeAccessToken";
            $scope.concernRequest.offset = offset;
            $scope.concernRequest.limit = limit;
            $scope.concernRequest.archived = archived;

            $scope.previousPage();
        });

        /**
         * Test to check that the previousPage request is ignored if it would result in a negative page offset.
         */
        it('Invalid previous page test', function() {

            // Mock out location to catch the page redirect
            var $location = {
                url: function(path) {}
            };
            spyOn($location, 'url');

            var controller = $controller('InboxCtrl', { $scope: $scope, $location: $location, firebase: firebaseMock, adminApi: adminApiMock });

            $scope.concernRequest.accessToken = "fakeAccessToken";
            $scope.concernRequest.offset = 0;
            $scope.concernRequest.limit = 25;
            $scope.concernRequest.archived = false;

            $scope.previousPage();

            expect($location.url).not.toHaveBeenCalled();
        });
    });

    /**
     * Unit tests for the concern selection functionality of the inbox controller.
     * This is related to when a concern in the inbox is clicked.
     */
    describe('Concern selection tests', function() {

        /**
         * Test to check that the previousPage request is ignored if it would result in a negative page offset.
         */
        it('Invalid previous page test', function() {

            // Mock out location to catch the page redirect
            var $location = {
                url: function(path) {}
            };
            spyOn($location, 'url');

            var controller = $controller('InboxCtrl', { $scope: $scope, $location: $location, firebase: firebaseMock, adminApi: adminApiMock });

            var concern = {
                id : 1234567
            };
            $scope.concernSelected(concern);

            expect($location.url).toHaveBeenCalledWith('/concern-detail/1234567');
        });
    });

    /**
     * Tests that the most recent status is properly loaded for each concern in the inbox controller.
     */
    describe('Concern data tests', function() {

        /**
         * Test that an error is thrown when attempting to get the current status for a null concern.
         */
        it('Status for null concern test', function() {

            $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });

            expect(function() {
                $scope.currentStatus(null);
            }).toThrow(new Error("Attempted to get the status of a null concern."));

        });

        /**
         * Test that an error is thrown when attempting to get the current status for a concern with no statuses.
         */
        it('Status for concern with no statuses', function() {

            $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });

            expect(function() {
                var concern = {
                    statuses : []
                };
                $scope.currentStatus(concern);
            }).toThrow(new Error("Attempted to get the status of a concern with no statuses."));
        });

        /**
         * Test that the most recent status is returned when attempting to get the current status for a valid concern.
         */
        it('Status for valid concern', function() {

            $controller('InboxCtrl', { $scope: $scope, firebase: firebaseMock, adminApi: adminApiMock });

            // Mock out the call to the root controller
            $scope.statusNames = function(key) {
                if (key == 'RESOLVED') {
                    return 'Resolved';
                }
            };

            var concern = {
                statuses : [
                    {type : "PENDING"},
                    {type : "RESOLVED"}
                ]
            };
            expect($scope.currentStatus(concern)).toEqual("Resolved");
        });
    });
});

