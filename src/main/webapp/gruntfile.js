module.exports = function(grunt) {

    grunt.file.preserveBOM = true;
    grunt.file.defaultEncoding = 'utf8';

    // Load all of our NPM tasks
    // Make sure you add the task package to the 'package.json' file
    // and run 'npm install' before you add the package here
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-html2js');
    grunt.loadNpmTasks('grunt-contrib-compass');
    grunt.loadNpmTasks('grunt-ngmin');
    grunt.loadNpmTasks("grunt-remove-logging");
    grunt.loadNpmTasks('grunt-contrib-imagemin');
    grunt.loadNpmTasks('grunt-cache-bust');

    // Default task, so if you just run 'grunt', this is what it will do
    grunt.registerTask('default', ['build']);

    // General build task, for dev only
    grunt.registerTask('build', [
        'clean:preBuild',
        'html',
        'templates',
        'js',
        'css',
        'copy:assets',
        'clean:postBuild'
    ]);

    // Release build for production
    grunt.registerTask('release', [
        'clean:preBuild',
        'html2js',
        'concat:prod',
        'removelogging',
        'ngmin',
        'uglify',
        'compass:prod',
        'copy',
        'imagemin',
        'cacheBust',
        'clean:postBuild'
    ]);

    // Utility tasks, these are primarily used by the watchers and the dev build
    grunt.registerTask('css', ['clean:css']);
    grunt.registerTask('js', [/*'jshint',*/'clean:js','templates','concat:dev']);
    grunt.registerTask('templates', ['clean:templates', 'html2js', 'concat:dev', 'copy:assets']);
    grunt.registerTask('html', ['copy:html','copy:assets']);

    // Print a timestamp, this help you determine the last build
    // with a quick glance at the terminal
    grunt.registerTask('timestamp', function() {
        grunt.log.subhead(Date());
    });

    grunt.packageInfo = grunt.file.readJSON('package.json');

    // Project configuration.
    grunt.initConfig({
        distdir: 'dist',
        pkg: grunt.file.readJSON('package.json'),
        banner:
        '/*! <%= pkg.title || pkg.name %> - version:<%= pkg.version %> - <%= grunt.template.today("yyyy-mm-dd") %>' +
        '<%= pkg.homepage ? " * " + pkg.homepage : "" %>' +
        ' * Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author %>;*/\n',

        src: {
            js: ['js/**/*.js'],
            i18n: [ 'src/locale/**/*.js'],
            specs: ['test/**/*.spec.js'],
            scenarios: ['test/**/*.scenario.js'],
            html: ['src/*.html'],
            tpl: {
                app: ['src/app/components/**/*.tpl.html'],
                common: ['src/app/common/**/*.tpl.html'],
                js: ['<%= distdir %>/templates/**/*.js']
            },
            sass: ['src/sass/**/*.scss'],
            images: 'src/images'
        },

        clean: {
            preBuild: ['<%= distdir %>/*'],
            postBuild: ['<%= distdir %>/temp','<%= distdir %>/templates'],
            css: ['<%= distdir %>/css'],
            js: ['<%= distdir %>/scripts'],
            templates: ['<%= distdir %>/templates']
        },

        copy: {
            assets: {
                files: [
                    {dest: '<%= distdir %>', src : '**', expand: true, cwd: 'src/assets/'},
                    {dest: '<%= distdir %>/scripts', src : '**', expand: true, cwd: 'vendor'}

                ]
            },
            css: {
                files: [
                    {dest: '<%= distdir %>/css', src:'**', expand: true, cwd:'<%= distdir %>/temp/css/',
                        rename: function(dest, src) {
                            if (src === "main.css") {
                                return dest + "/" + grunt.packageInfo.name + "-" + grunt.packageInfo.version + ".css";
                            }

                            return dest + "/" + src;
                        }

                    }
                ]
            },
            cssFonts: {
                files: [
                    {dest: '<%= distdir %>/css/uigrid', src : ['*.*', '!*.scss'], expand: true, cwd: 'src/sass/uigrid'}
                ]
            },
            html: {
                options: {
                    processContent: function (content, srcpath) {
                        return grunt.template.process(content);
                    }
                },
                files: [{dest:'<%= distdir %>', src:'*.html', expand: true, cwd:'src'}]
            }
        },

        imagemin: {
            all: {
                options: {
                    optimizationLevel: 3
                },
                files: [{
                    expand: true,
                    cwd: '<%= distdir %>/images',
                    src: ['**/*.{png,jpg,gif}'],
                    dest: '<%= distdir %>/images'
                }]
            }
        },

        html2js: {
            app: {
                options: {
                    base: 'src/app/components'
                },
                src: ['<%= src.tpl.app %>'],
                dest: '<%= distdir %>/templates/app.js',
                module: 'templates.app'
            },
            common: {
                options: {
                    base: 'src/app/common'
                },
                src: ['<%= src.tpl.common %>'],
                dest: '<%= distdir %>/templates/common.js',
                module: 'templates.common'
            }
        },

        concat:{
            dev:{
                options: {
                    banner: "<%= banner %>"
                },
                src:['<%= src.js %>', '<%= src.tpl.js %>'],
                dest:'<%= distdir %>/scripts/<%= pkg.name %>-<%= pkg.version %>.js'
            },
            prod:{
                src:['<%= src.js %>',  '<%= src.tpl.js %>'],
                dest:'<%= distdir %>/temp/<%= pkg.name %>-<%= pkg.version %>.concat.js'
            }
        },

        removelogging: {
            dist: {
                src: '<%= distdir %>/temp/<%= pkg.name %>-<%= pkg.version %>.concat.js',
                dest: '<%= distdir %>/temp/<%= pkg.name %>-<%= pkg.version %>.clean.js',

                options: {
                }
            }
        },

        ngmin: {
            all: {
                src: ['<%= distdir %>/temp/<%= pkg.name %>-<%= pkg.version %>.clean.js'],
                dest: '<%= distdir %>/temp/<%= pkg.name %>-<%= pkg.version %>.ngmin.js'
            }
        },

        uglify: {
            options: {
                banner: "<%= banner %>",
                mangle: true
            },
            dist:{
                files: {
                    '<%= distdir %>/scripts/<%= pkg.name %>-<%= pkg.version %>.js': '<%= distdir %>/temp/<%= pkg.name %>-<%= pkg.version %>.ngmin.js'
                }
            }
        },

        watch:{
            js: {
                files: ['<%= src.js %>'],
                tasks: ['js', 'timestamp']
            },
            css: {
                files: ['<%= src.sass %>'],
                tasks: ['css', 'timestamp']
            },
            templates: {
                files: ['<%= src.tpl.app %>','<%= src.tpl.common %>'],
                tasks: ['templates', 'timestamp']
            },
            html: {
                files: ['<%= src.html %>'],
                tasks: ['html', 'timestamp']
            }
        },

        /*jshint:{
              files: [
                'Gruntfile.js',
                'js/controllers*//*.js'
              ],

            options: {
                jshintrc: '.jshintrc',
                smarttabs: true
            }
        },*/

        /*compass: {
            prod: {
                options: {
                    outputStyle: 'compressed',
                    cssDir: '../../<%= distdir %>/temp/css',
                    sassDir: '',
                    basePath: 'src/sass',
                    environment: 'production',
                    noLineComments: true,
                    force: true
                }
            },
            dev: {
                options: {
                    cssDir: '../../<%= distdir %>/temp/css',
                    sassDir: '',
                    basePath: 'src/sass',
                    noLineComments: true,
                    fontsDir: '/fonts'
                }
            }
        },*/

        cacheBust: {
            options: {
                encoding: 'utf8',
                algorithm: 'md5',
                length: 16
            },
            assets: {
                files: [{
                    src: ['<%= distdir %>/index.html']
                }]
            }
        }
    });
};
