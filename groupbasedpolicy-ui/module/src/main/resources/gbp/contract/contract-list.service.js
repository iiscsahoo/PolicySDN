define([], function () {
    'use strict';

    angular.module('app.gbp').service('ContractListService', ContractListService);

    ContractListService.$inject = ['Restangular', 'ContractService'];

    function ContractListService(Restangular, ContractService) {
        /* methods */
        this.createList = createList;

        function ContractList() {
            /* properties */
            this.data = [];

            /* methods */
            this.setData = setData;
            this.get = get;

            /* Implementation */
            /**
             * fills ContractList object with data
             * @param data
             */
            function setData(data) {
                var self = this;
                data.forEach(function (dataElement) {
                    self.data.push(ContractService.createObject(dataElement));
                });
            }

            function get(tenantId) {
                /* jshint validthis:true */
                var self = this;

                // var restObj = Restangular.one('restconf').one(dataStore).one('policy:tenants')
                // .one('tenant').one('tenant1').one('policy');
                var restObj = Restangular.one('restconf').one('config').one('policy:tenants')
                    .one('tenant').one(tenantId).one('policy');

                return restObj.get().then(function (data) {
                    // if (data.policy.contract) {
                    //     self.setData(data.policy.contract);
                    self.setData(data.policy.contract);
                });
            }
        }

        function createList() {
            var obj = new ContractList();

            return obj;
        }
    }

    return ContractListService;
});
