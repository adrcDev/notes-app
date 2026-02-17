/*This file is not used by webpack. This file is just
for reference. The webpack.common.js,webpack.dev.js and webpack.prod.js were created using this file as a reference.*/
const path = require('path');

/* Used in order to make webpack automatically generate an html file
 and add the script tag to it pointing to the ouput bundled javascript file */
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

/*Plugin for react hmr */
const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');

module.exports = {
  entry: {
    index: "./src/index.js",
  },
  output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'dist'),
    clean: true, //cleans the output directory before each build
    /*Tell webpack what kind of ES-features may be used in the generated runtime-code. Added so that webpack never uses arrow functions in the
    code inserted by it inside the bundle */
    environment: {
      arrowFunction: false // <-- this line does the trick
    },
    /* The same as output.filename but for Asset Modules. Specifies where to emit the the files processed by asset/resource asset module within the build's output directory (dir in this case)*/
    // assetModuleFilename: 'images/[hash][ext][query]',

    /*Required for react-router to work*/
    publicPath: "/",
  },
  mode: "development",
  devtool: "eval-source-map",
  watchOptions: {
    ignored: /node_modules/,
  },

  //Below property configures webpack-dev-server.
  /*http://localhost:9000/webpack-dev-server will show where files are
  served */
  devServer: { 
    // static: './dist',
    /* Enable/disable gzip compression for everything served */
    // compress: true, 
    // port: 9000,
    
    client: {
      /*Shows a full-screen overlay in the browser when there are compiler errors or runtime errors or warnings*/
      overlay:false,
      //Prints compilation progress in percentage in the browser.
      progress: false,
    },

    //Adds headers to all responses
    // headers: {
    //   'X-Custom-Foo': 'bar',
    // },

    /*Enable webpack's Hot Module Replacement feature:
    values are boolean,"only" (enable hmr without page refreshes). */
    hot: true,

    /*Tells dev-server to open the browser after server had been started.
    accepts-boolean string object. String can be absolute url or a path
    string*/
    open:["/"],

    /*Allows to set server and options (by default 'http'). Allowed values - 'http' | 'https' | 'spdy' string object */
    // server: "https",

    /*By default, the dev-server will reload/refresh the page when file changes are detected. devServer.hot option must be disabled or devServer.watchFiles option must be enabled in order for liveReload to take effect. Disable devServer.liveReload by setting it to false */
    // liveReload: false,

    /*This option allows you to configure a list of globs/directories/files to watch for file changes.*/
    // watchFiles: {
    //   paths: [],
    //   options: {
    //   },
    // },
 
    /*Controls whether the generated output files are actually overwritten
      when changes are made while the webpack dev server is running. If 
      writeToDisk is false then the webpack compilation process only happens in memory (RAM) and no actual output is written to disk (this is the default).*/
    // devMiddleware: {
    //   writeToDisk: true
    // },

    /*Required for react-router library to work. The BrowserRouter component and createBrowserRouter() make use of the history web api, so this option must be enabled. This causes webpack-dev-server to always serve index.html file instead of 404 http response whenever an invalid path segment is used. So in the case of a react app, for every path segment, the 
    index.html file is served which in turn loads the javascript bundle which in turn gives control 
    to react-router library for reading the path segment in the url and rendering the correct component. */
    historyApiFallback: true,

  },
 
  /* Specifying loaders and asset modules (inbuilt loaders) to use */
  module: {               
    rules: [
      {
        test: /\.css$/i, 
        use: [
        {
         loader:"style-loader",
         options: {
          }
        }
        ,
        {
          loader:"css-loader",
          options: {

            /*The option importLoaders allows you to configure how many loaders before css-loader should be applied to @import(css at rule) resources and CSS modules/ICSS imports. 
             // 0 => no loaders (default);
              // 1 => postcss-loader;
              // 2 => postcss-loader, sass-loader*/
              // importLoaders: 1,
              
            /* This enables compilation for the css modules specification
            for all the css files. Basically the css files will be preprocessed by the css modules preprocesser that is part of the css-loader package. By default (if modules property is not declared) the value of modules is undefined due to which only filenames ending with .module.css are compiled as css modules.(eg style.module.css). Set modules: true or set modules to an object with mode property set to a value of "local" to enable css modules preprocessing for all files ending with .css (all css files). By default or if you set modules: true or modules: "local", all the
            styles declared within a css file (or .module.css file when modules property is not specified) will be local to that
            specific javascript module(file) in which it is imported.
            There is no need to use webpack's :local(.classname) in order 
            to enforce this. If you want to  declare a selector that will work globally you use :global(.classname). Css-loader will
            assign a unique name via a hashing function to each local class whereas it won't do so for global classes.  */
            modules: {
              /* Setting mode property ot "local" ensures that all the selectors declared in the css files are local by default i.e., treated as having been written as
              :local(<selector>). If you want to declare global styles then you will have to explicity write :global(<selector>) */
              mode: "local",
			  
              /*This ensure thats css modules preprocessing will only be enabled for files having exactly 2 parts separated by a dot and the second part is css.I had to add this to disable css module preprocessing for file names like "tailwind.import.css" so that i could import the tailwind
              classes without the utility class names being converted to the hash format(see localIdentName property)*/
              auto: /^([^\.]+)\.css$/,
              
              /* Used in order to change the format of the auto generated class and id
            hashes. Only set this property for development, don't set for production */
            localIdentName: "[path][name]__[local]--[hash:base64]",

            },

            /*Default: depends on the compiler.devtool value
By default generation of source maps depends on the devtool option. All values enable source map generation except eval and false value           */
            //sourceMap: true,

            
            

            
          }
        },
        
        ]
      },

      {
        test: /\.(?:js|mjs|cjs)$/,
        exclude: {
          // Exclude libraries in node_modules ...
          and: [/node_modules/], 
          /*Except for a few of them that needs to be transpiled because they use modern syntax (not already transpiled before) */
          not: []
        },
        use: {
          loader: 'babel-loader',
          /*see Babel's official website for a detailed reference 
          of Babel's config options */
          options: {
            /*These two cache- options are part of babel-loader's config
            options and not part of babel's config options. */
            cacheDirectory: true,
            cacheCompression: false, //default is true

            /*A preset is a set of plugins.Preset ordering is reversed (last to first). eg: "presets": ["a", "b", "c"] will
            will run in the following order: c, b, then a.*/
            presets: [
              ['@babel/preset-env',{
                /*Outputs to console.log the polyfills and transform plugins enabled by preset-env and, if applicable, which one of your targets that needed it.*/
                // debug: true,

                /*This option enables a new plugin that replaces the import "core-js/stable"; and require("core-js"); statements with individual imports to different core-js entry points based on environment. (based on targets property or browserlist(don't know what this is))*/
                useBuiltIns: "entry",
                corejs: "3.39.0"
              }],
              ['@babel/preset-react',{
                /*This toggles behavior specific to development, such as adding __source and __self to the jsx tags */
                // development: true,

                /*Decides which runtime to use.automatic auto imports the functions that JSX transpiles to. classic does not automatic import anything.defaults to classic */
                // runtime: automatic,

                /*Replace the function used when compiling JSX expressions. It should be a qualified name (e.g. React.createElement) or an identifier (e.g. createElement).
                string, defaults to React.createElement.*/
                // pragma: ,

                /*Replace the component used when compiling JSX fragments. It should be a valid JSX tag name.string, defaults to React.Fragment.*/
                // pragmaFrag: ,
              }]  
            ],

            //setting babel plugin for react hmr.(This plugin should only be enabled in development build config)
            plugins: [require.resolve('react-refresh/babel')],  

            /*When no targets are specified: Babel will assume you are targeting the oldest browsers possible. For example, @babel/preset-env will transform all ES2015-ES2020 code to be ES5 compatible.We recommend setting targets to reduce the output code size. Supported value types: string | Array<string> | 
            { [string]: string }*/
            //targets: ,

            //don't know if this is required
            //sourceMaps: true


          }
        }
      },

      /*The below rule uses webpack's asset modules (in built loader).type="asset" implies that webpack will automatically choose between type="asset/resource" and type="asset/inline" by following a default condition: a file with size less than 8kb will be treated as a inline module type and resource asset module type otherwise.*/
      {
        test: /\.(png|gif|jpe?g|jfif|svg|webp)$/,
        type: "asset",
        /*Specifies where the image files will be emitted inside the
        build's output directory (dir in this case) when type="asset/resource" is used as the image file size is > 8kb.*/
        generator: {
          filename: 'images/[hash][ext][query]'
        }
      },

      {
        test: /\.(ttf|otf|woff|woff2|eot)$/,
        type: "asset",
        generator: {
          filename: 'static/fonts/[hash][ext][query]'
        }
      },
    ]
  },

  //ReactRefreshWebpackPlugin() should only be added in development
  // build config(to do). This plugin is used in order to enable HMR for
  // react apps.
  plugins: [new HtmlWebpackPlugin({
      title: "sample page generated using webpack",
      inject: "head",
      minify: false,
      template: "./index_template.html",
      fileName: "index.html"
    }),new ReactRefreshWebpackPlugin({
      overlay: false, // Disable the error overlay in the browser
    })] ,

    //stats: {
      //generate debug logs for babel-loader 
      // loggingDebug: ["babel-loader"] //
    //},

  

    /* If there are multiple entry and points and if they import
    common modules (have common dependencies) then setting the below
    property will help avoid duplication of these common modules in 
    the respective bundles by creating a separate bundle for the
    common dependencies */
    // optimization: {
    //   splitChunks: {
    //     chunks: 'all',
    //   },
    // },
  
};






/* Use style-loader for development and  mini-css-extract-plugin for production. Never use them together */
/*Below are loader definitions for style-loader and mini-css-extract-
plugin's loaders */

// {
//   loader:"style-loader",
//   options: {
//   }
// }

// {
//   loader: MiniCssExtractPlugin.loader,
//   options: {

//   }
// }


// below are plugins

// new MiniCssExtractPlugin(),
//     new HtmlWebpackPlugin({
//       title: "sample page generated using webpack",
//       inject: "head",
//       minify: false,
//       template: "./index.html"
//   })