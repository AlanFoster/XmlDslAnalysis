"use strict";

(function () {
    window.browserTrigger = window.browserTrigger || function() { };
})();

describe("Image uploader directive", function () {
    var element;
    var $scope;
    var ctrl;

    beforeEach(module("docsApp"));
    //beforeEach(module("templates"));
    beforeEach(inject(function ($compile, $rootScope) {
        $scope = $rootScope;
        $scope.images = [];
        element = angular.element("<image-uploader images='images'></image-uploader>")
        element = $compile(element)($rootScope)
        //$scope.$digest();

        ctrl = element.controller("imageUploader");
    }));

    describe("image array scope", function () {
        it("should have a default empty image array", function () {
            expect($scope.images.length).toBe(0)
        });

        it("should not be undefined", function () {
            expect($scope.images).not.toBe(undefined)
        })
    });

    describe("Update class properties when dragged over target", function () {
        xit("should have a dragged class name when dragged over", function () {
            browserTrigger(element, "dragover");
            expect(element.hasClass("dragged")).toBe(true);
        })

        it("should not have the dragged class by default", function () {
            expect(element.hasClass("dragged")).toBe(false);
        })

        it("should have a dragged class name when dragged out", function () {
            browserTrigger(element.find(".target"), "dragover");
            browserTrigger(element.find(".target"), "dragout");
            expect(element.hasClass("dragged")).toBe(false);
        })

        it("should not modify the scope when dragged over", function () {
            browserTrigger(element.find(".target"), "dragover");
            expect($scope.images.length).toBe(0);
        })
    });

    xdescribe("deleting mechanism", function () {
        var elemScope;

        beforeEach(function() {
            elemScope = element.scope();
        })

        it("should delete as expected when an image exists and there are no images", function () {
            elemScope.deleteImage({});
            expect(elemScope.images.length).toBe(0)
        })

        it("should delete successfully in the right place", function () {
            elemScope.images = [
                {
                    location: "images/contribution.png",
                    title: "Title 1",
                    description: "Description 1"
                },
                {
                    location: "images/paramInsight.png",
                    title: "Param Insight",
                    description: "Param Insight Description"
                }
            ];
            elemScope.deleteImage(images[1]);
            expect(elemScope.images.length).toBe(1);
            expect(elemScope.images[0].title).toBe("Title 1");
        })
    });
});


