require.config({
    paths: {
        'angular-material': 'app/gbp/vendor/angular-material/angular-material.min',
        'angular-animate': 'app/gbp/vendor/angular-animate/angular-animate.min',
        'angular-aria': 'app/gbp/vendor/angular-aria/angular-aria.min',
        'lodash': 'app/gbp/vendor/lodash/dist/lodash.min',
        'angular-material-data-table': 'app/gbp/vendor/angular-material-data-table/dist/md-data-table.min',
    },
    shim: {
        'angular-material': ['angular'],
        'angular-animate': ['angular'],
        'angular-aria': ['angular'],
        'angular-material-data-table': ['angular', 'angular-material'],
    },
});

define(['app/gbp/common/gbp.module']);
